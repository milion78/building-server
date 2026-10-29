package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.MapPoint;
import com.tencent.wxcloudrun.model.RelicInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tencent.wxcloudrun.util.CoordinateUtil;
import com.tencent.wxcloudrun.util.IdCodeUtil;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 地图服务类
 * 
 * @author building-service
 */
@Service
public class MapService {

    private static final Logger logger = LoggerFactory.getLogger(MapService.class);

    private final RelicInfoService relicInfoService;
    private final FileService fileService;

    public MapService(@Autowired RelicInfoService relicInfoService,
                      @Autowired FileService fileService) {
        this.relicInfoService = relicInfoService;
        this.fileService = fileService;
    }

    /**
     * 加载地图页面点位信息
     * 按视野四角查询文保点，并转换成地图点位
     * 
     * @param topLeft 地图左上角经纬度
     * @param bottomRight 地图右下角经纬度
     * @return 点位信息列表
     */
    public List<MapPoint> loadPage(String topLeft, String bottomRight) {
        List<MapPoint> points = new ArrayList<>();
        logger.info("MapService.loadPage - topLeft: {}, bottomRight: {}", topLeft, bottomRight);
        Double leftLongitude = CoordinateUtil.getLongitude(topLeft);
        Double topLatitude = CoordinateUtil.getLatitude(topLeft);
        Double rightLongitude = CoordinateUtil.getLongitude(bottomRight);
        Double bottomLatitude = CoordinateUtil.getLatitude(bottomRight);
        if (leftLongitude == null || topLatitude == null || rightLongitude == null || bottomLatitude == null) {
            logger.warn("坐标解析失败，topLeft: {}, bottomRight: {}", topLeft, bottomRight);
            return points;
        }
        List<RelicInfo> relics = relicInfoService.listRelicInfoByLocation(
                leftLongitude, topLatitude, rightLongitude, bottomLatitude);
        for (RelicInfo relic : relics) {
            if (relic.getLongitude() == null || relic.getLatitude() == null) {
                continue;
            }
            String coordinate = CoordinateUtil.formatCoordinate(relic.getLongitude(), relic.getLatitude());
            if (coordinate == null) {
                continue;
            }
            MapPoint point = new MapPoint();
            point.setId(IdCodeUtil.toCode(relic.getId()));
            point.setName(relic.getRelicName());
            point.setCoordinate(coordinate);
            String fileId = fileService.getFileIdByUrl(relic.getCoverImage());
            point.setPicCloudId(fileId);
            point.setPicUrl(fileService.getUrlByFileId(fileId));
            point.setRelicRank(relic.getRelicRank());
            point.setRelicAddress(relic.getRelicAddress());
            points.add(point);
        }
        logger.info("MapService.loadPage - count: {}", points.size());
        return points;
    }
}

