package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.MapPoint;
import org.springframework.stereotype.Service;
import com.tencent.wxcloudrun.util.CoordinateUtil;
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

    /**
     * 加载地图页面点位信息
     * 使用 mock 数据，根据请求参数返回写死的点位数据
     * 
     * @param topLeft 地图左上角经纬度
     * @param bottomRight 地图右下角经纬度
     * @return 点位信息列表
     */
    public List<MapPoint> loadPage(String topLeft, String bottomRight) {
        // Mock 数据，按照接口文档示例写死
        List<MapPoint> points = new ArrayList<>();

        Double leftLongitude = CoordinateUtil.getLongitude(topLeft);
        Double topLatitude = CoordinateUtil.getLatitude(topLeft);
        Double rightLongitude = CoordinateUtil.getLongitude(bottomRight);
        Double bottomLatitude = CoordinateUtil.getLatitude(bottomRight);
        if (leftLongitude == null || topLatitude == null || rightLongitude == null || bottomLatitude == null) {
            return new ArrayList<>();
        }
        int count =(int)Math.random()*10;
        logger.info("count: {}", count);
        for (int i = 0; i < count; i++) {
            double longitude = leftLongitude + Math.random() * (rightLongitude - leftLongitude);
            double latitude = topLatitude + Math.random() * (bottomLatitude - topLatitude);
            MapPoint point = new MapPoint();
            point.setCoordinate(CoordinateUtil.formatCoordinate(longitude, latitude));
            point.setName("卢沟桥" + i);
            point.setId(i+1L);
            point.setPicUrl("https://mp.weixin.qq.com/wxopen/basicprofile?action=get_headimg&token=50698586&t=1763474422323");
            points.add(point);
            logger.info("point: {}", point);
        }    

        return points;
    }
}

