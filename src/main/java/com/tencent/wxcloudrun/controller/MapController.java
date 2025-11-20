package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.common.ResponseResult;
import com.tencent.wxcloudrun.model.MapPoint;
import com.tencent.wxcloudrun.service.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地图接口控制器
 * 一级路径：/map
 * 
 * @author building-service
 */
@RestController
@RequestMapping("/map")
public class MapController {
    
    private static final Logger logger = LoggerFactory.getLogger(MapController.class);
    
    @Autowired
    private MapService mapService;
    
    /**
     * 加载地图页面点位信息
     * 接口地址: POST /map/loadPage
     * 
     * @param topLeft 地图左上角经纬度，格式："{经度,纬度}"
     * @param bottomRight 地图右下角经纬度，格式："{经度,纬度}"
     * @return 统一响应格式，包含点位信息列表
     */
    @PostMapping("/loadPage")
    public ResponseResult<List<MapPoint>> loadPage(
            @RequestParam(required = false) String topLeft,
            @RequestParam(required = false) String bottomRight) {
        logger.info("/map/loadPage post request, topLeft: {}, bottomRight: {}", topLeft, bottomRight);
        
        List<MapPoint> points = mapService.loadPage(topLeft, bottomRight);
        return ResponseResult.success(points);
    }
}

