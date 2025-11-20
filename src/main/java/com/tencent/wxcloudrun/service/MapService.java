package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.MapPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图服务类
 * 
 * @author building-service
 */
@Service
public class MapService {
    
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
        
        MapPoint point1 = new MapPoint();
        point1.setCoordinate("{116.443750,39.920900}");
        point1.setName("卢沟桥");
        point1.setId(12L);
        point1.setPicUrl("https://mp.weixin.qq.com/wxopen/basicprofile?action=get_headimg&token=50698586&t=1763474422323");
        points.add(point1);
        
        MapPoint point2 = new MapPoint();
        point2.setCoordinate("{116.443650,39.921000}");
        point2.setName("卢沟桥2");
        point2.setId(13L);
        point2.setPicUrl("https://mp.weixin.qq.com/wxopen/basicprofile?action=get_headimg&token=50698586&t=1763474422323");
        points.add(point2);
        
        return points;
    }
}

