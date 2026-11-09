package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.RelicDetail;
import com.tencent.wxcloudrun.model.PicInfo;
import com.tencent.wxcloudrun.model.RelicInfo;
import com.tencent.wxcloudrun.model.RelicInfoUpdate;
import com.tencent.wxcloudrun.service.entity.PicInfoService;
import com.tencent.wxcloudrun.service.entity.RelicInfoUpdateService;
import com.tencent.wxcloudrun.typehandler.ImageUrlsTypeHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 文保详情组装。
 * 数据库读写仍由 RelicInfoService 负责。
 */
@Service
public class RelicService {

    private static final String GALLERY_TYPE = "Gallery";

    private final FileService fileService;
    private final PicInfoService picInfoService;
    private final RelicInfoUpdateService relicInfoUpdateService;

    public RelicService(@Autowired FileService fileService,
                        @Autowired PicInfoService picInfoService,
                        @Autowired RelicInfoUpdateService relicInfoUpdateService) {
        this.fileService = fileService;
        this.picInfoService = picInfoService;
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
        if (update.getCoverImage() != null && !update.getCoverImage().trim().isEmpty()) {
            detail.setCoverImage(fileService.getFileIdByUrl(update.getCoverImage()));
        }
        detail.setImageUrls(listGalleryFileIds(relic.getSourceId()));
        return detail;
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
            String fileId = fileService.getFileIdByUrl(url);
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
            String fileId = fileService.getFileIdByUrl(pic.getUrl());
            if (fileId != null && !fileId.trim().isEmpty()) {
                fileIds.add(fileId);
            }
        }
        return fileIds;
    }
}
