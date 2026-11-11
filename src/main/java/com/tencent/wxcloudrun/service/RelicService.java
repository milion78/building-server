package com.tencent.wxcloudrun.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.wxcloudrun.dto.Footprint;
import com.tencent.wxcloudrun.dto.RelicDetail;
import com.tencent.wxcloudrun.dto.RelicFootprintInfo;
import com.tencent.wxcloudrun.model.PicInfo;
import com.tencent.wxcloudrun.model.RelicInfo;
import com.tencent.wxcloudrun.model.RelicInfoUpdate;
import com.tencent.wxcloudrun.service.entity.PicInfoService;
import com.tencent.wxcloudrun.service.entity.RelicInfoService;
import com.tencent.wxcloudrun.service.entity.RelicInfoUpdateService;
import com.tencent.wxcloudrun.typehandler.ImageUrlsTypeHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 文保详情组装。
 * 数据库读写仍由 RelicInfoService 负责。
 */
@Service
public class RelicService {

    private static final String GALLERY_TYPE = "Gallery";

    private static final String FOOTPRINT_TYPE = "Footprint";

    /** 足迹照片与文保点距离不超过该值，视为在现场 */
    private static final double ON_SITE_METERS = 500d;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final FileService fileService;
    private final PicInfoService picInfoService;
    private final RelicInfoService relicInfoService;
    private final RelicInfoUpdateService relicInfoUpdateService;

    public RelicService(@Autowired FileService fileService,
                        @Autowired PicInfoService picInfoService,
                        @Autowired RelicInfoService relicInfoService,
                        @Autowired RelicInfoUpdateService relicInfoUpdateService) {
        this.fileService = fileService;
        this.picInfoService = picInfoService;
        this.relicInfoService = relicInfoService;
        this.relicInfoUpdateService = relicInfoUpdateService;
    }

    /**
     * 把数据库记录转成接口详情。
     * 有 sourceId 且能查到更新记录时，用更新记录覆盖属性，图片按 sourceId 取 Gallery。
     * 查不到时，使用文保记录自身的 imageUrls。
     */
    public RelicDetail toRelicDetail(RelicInfo relic) {
        RelicDetail detail = new RelicDetail();
        if (relic == null) {
            return detail;
        }
        BeanUtils.copyProperties(relic, detail);
        RelicInfoUpdate update = findUpdate(relic.getSourceId());
        if (update == null) {
            detail.setImageUrls(toImageFileIds(relic.getImageUrls()));
            return detail;
        }
        Long relicId = relic.getId();
        BeanUtils.copyProperties(update, detail);
        detail.setId(relicId);
        detail.setEra(update.getEra());
        detail.setBatch(readBatch(update.getRelic()));
        if (update.getCoverImage() != null && !update.getCoverImage().trim().isEmpty()) {
            detail.setCoverImage(fileService.getThumbFileIdByUrl(update.getCoverImage()));
        }
        detail.setImageUrls(listGalleryFileIds(relic.getSourceId()));
        detail.setFootprints(aggregateFootprints(relic, listFootprintPics(relic)));
        return detail;
    }

    /**
     * 查询文保足迹汇总：去重用户数 + 年份列表（从大到小）。
     */
    public RelicFootprintInfo getFootprintInfo(Long relicId) {
        RelicFootprintInfo info = new RelicFootprintInfo();
        List<Footprint> footprints = listFootprints(relicId);
        info.setCount(countDistinctUsernames(footprints));
        info.setYearList(listFootprintYearsDesc(footprints));
        return info;
    }

    /**
     * 查询文保足迹。图片按 sourceId 查 pic_info 中 type 为 Footprint 的记录。
     * 没有 sourceId 时用文保 id。头像和照片转成云存储 fileId。
     * 相同 username、userhead 合并为一条，imgUrl 收集全部照片，actionTime 取更新时间的平均值。
     */
    public List<Footprint> listFootprints(Long relicId) {
        if (relicId == null) {
            return new ArrayList<Footprint>();
        }
        RelicInfo relic = relicInfoService.getRelicInfo(relicId).orElse(null);
        if (relic == null) {
            return new ArrayList<Footprint>();
        }
        return aggregateFootprints(relic, listFootprintPics(relic));
    }

    /**
     * 按 username 和 userhead 聚合。同一组的照片 url 放入同一个 imgUrl，
     * actionTime 为该组 updateTime 的平均值。
     * 组内任一照片坐标落在文保点范围内，则 onSite 为 1。
     */
    private List<Footprint> aggregateFootprints(RelicInfo relic, List<PicInfo> pics) {
        if (pics == null || pics.isEmpty()) {
            return new ArrayList<Footprint>();
        }
        Map<String, Footprint> grouped = new LinkedHashMap<String, Footprint>();
        Map<String, List<PicInfo>> groupedPics = new LinkedHashMap<String, List<PicInfo>>();
        Map<String, long[]> timeStats = new LinkedHashMap<String, long[]>();
        for (PicInfo pic : pics) {
            if (pic == null) {
                continue;
            }
            String username = pic.getUseeNickname() == null ? "" : pic.getUseeNickname().trim();
            String userhead = toUserheadFileId(pic.getUserHeadUrl());
            String key = username + "\u0000" + (userhead == null ? "" : userhead);
            Footprint footprint = grouped.get(key);
            if (footprint == null) {
                footprint = new Footprint();
                footprint.setUsername(username.isEmpty() ? null : username);
                footprint.setUserhead(userhead);
                grouped.put(key, footprint);
                groupedPics.put(key, new ArrayList<PicInfo>());
                timeStats.put(key, new long[] {0L, 0L});
            }
            groupedPics.get(key).add(pic);
            String fileId = toImageFileId(pic.getUrl());
            if (fileId != null) {
                footprint.getImgUrl().add(fileId);
            }
            if (pic.getUpdateTime() != null) {
                long[] stats = timeStats.get(key);
                stats[0] += pic.getUpdateTime().getTime();
                stats[1] += 1L;
            }
        }
        List<Footprint> footprints = new ArrayList<Footprint>();
        for (Map.Entry<String, Footprint> entry : grouped.entrySet()) {
            Footprint footprint = entry.getValue();
            long[] stats = timeStats.get(entry.getKey());
            if (stats[1] > 0L) {
                footprint.setActionTime(new Date(stats[0] / stats[1]));
            }
            footprint.setOnSite(resolveOnSite(relic, groupedPics.get(entry.getKey())));
            footprints.add(footprint);
        }
        footprints.sort(Comparator.comparing(Footprint::getActionTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return footprints;
    }

    private String toUserheadFileId(String userHeadUrl) {
        if (userHeadUrl == null || userHeadUrl.trim().isEmpty()) {
            return null;
        }
        String fileId = fileService.getThumbFileIdByUrl(userHeadUrl.trim());
        if (fileId == null || fileId.trim().isEmpty()) {
            return null;
        }
        return fileId;
    }

    private String toImageFileId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        String fileId = fileService.getThumbFileIdByUrl(url);
        if (fileId == null || fileId.trim().isEmpty()) {
            return null;
        }
        return fileId;
    }

    /**
     * 按 username 去重后的用户数量；空用户名不计入。
     */
    private int countDistinctUsernames(List<Footprint> footprints) {
        if (footprints == null || footprints.isEmpty()) {
            return 0;
        }
        Set<String> names = new HashSet<String>();
        for (Footprint footprint : footprints) {
            if (footprint == null || footprint.getUsername() == null) {
                continue;
            }
            String name = footprint.getUsername().trim();
            if (!name.isEmpty()) {
                names.add(name);
            }
        }
        return names.size();
    }

    /**
     * 从足迹 actionTime 提取年份，去重后按数字从大到小排列。
     */
    private List<Integer> listFootprintYearsDesc(List<Footprint> footprints) {
        if (footprints == null || footprints.isEmpty()) {
            return new ArrayList<Integer>();
        }
        TreeSet<Integer> years = new TreeSet<Integer>(Collections.reverseOrder());
        Calendar calendar = Calendar.getInstance();
        for (Footprint footprint : footprints) {
            if (footprint == null) {
                continue;
            }
            Date actionTime = footprint.getActionTime();
            if (actionTime == null) {
                continue;
            }
            calendar.setTime(actionTime);
            years.add(calendar.get(Calendar.YEAR));
        }
        return new ArrayList<Integer>(years);
    }

    /**
     * 从 relic JSON 读取 $.batch。字段缺失或内容不是 JSON 时返回 null。
     */
    private String readBatch(String relicJson) {
        if (relicJson == null || relicJson.trim().isEmpty()) {
            return null;
        }
        try {
            JsonNode batch = OBJECT_MAPPER.readTree(relicJson).path("batch");
            if (batch.isMissingNode() || batch.isNull()) {
                return null;
            }
            if (batch.isValueNode()) {
                return batch.asText();
            }
            return batch.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 按 sourceId 查询足迹照片；没有 sourceId 时用文保 id。
     */
    private List<PicInfo> listFootprintPics(RelicInfo relic) {
        if (relic == null) {
            return new ArrayList<PicInfo>();
        }
        Long picRelicId = relic.getSourceId() != null ? relic.getSourceId() : relic.getId();
        if (picRelicId == null) {
            return new ArrayList<PicInfo>();
        }
        List<PicInfo> pics = picInfoService.listPicInfoByRelicIdAndType(picRelicId, FOOTPRINT_TYPE);
        if (pics == null) {
            return new ArrayList<PicInfo>();
        }
        return pics;
    }

    /**
     * 这组照片里有一张落在文保点 500 米内则为 1，否则为 0。
     * pics 由调用方传入，避免同一次请求重复查询。
     */
    private int resolveOnSite(RelicInfo relic, List<PicInfo> pics) {
        if (relic == null || relic.getLongitude() == null || relic.getLatitude() == null) {
            return 0;
        }
        if (pics == null || pics.isEmpty()) {
            return 0;
        }
        for (PicInfo pic : pics) {
            if (pic.getLongitude() == null || pic.getLatitude() == null) {
                continue;
            }
            double meters = distanceMeters(relic.getLatitude(), relic.getLongitude(),
                    pic.getLatitude(), pic.getLongitude());
            if (meters <= ON_SITE_METERS) {
                return 1;
            }
        }
        return 0;
    }

    private static double distanceMeters(float lat1, float lng1, float lat2, float lng2) {
        double rad = Math.PI / 180d;
        double dLat = (lat2 - lat1) * rad;
        double dLng = (lng2 - lng1) * rad;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * rad) * Math.cos(lat2 * rad) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * 6371000d * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private RelicInfoUpdate findUpdate(Long sourceId) {
        if (sourceId == null) {
            return null;
        }
        return relicInfoUpdateService.getRelicInfoUpdateBySourceId(sourceId).orElse(null);
    }

    private List<String> toImageFileIds(String imageUrls) {
        List<String> fileIds = new ArrayList<>();
        for (String url : ImageUrlsTypeHandler.parse(imageUrls)) {
            String fileId = fileService.getThumbFileIdByUrl(url);
            if (fileId != null && !fileId.trim().isEmpty()) {
                fileIds.add(fileId);
            }
        }
        return fileIds;
    }

    private List<String> listGalleryFileIds(Long relicId) {
        List<String> fileIds = new ArrayList<>();
        if (relicId == null) {
            return fileIds;
        }
        for (PicInfo pic : picInfoService.listPicInfoByRelicIdAndType(relicId, GALLERY_TYPE)) {
            String fileId = fileService.getThumbFileIdByUrl(pic.getUrl());
            if (fileId != null && !fileId.trim().isEmpty()) {
                fileIds.add(fileId);
            }
        }
        return fileIds;
    }
}
