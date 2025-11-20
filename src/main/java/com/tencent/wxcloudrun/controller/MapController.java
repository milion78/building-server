package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.common.ResponseResult;
import com.tencent.wxcloudrun.dto.MapLoadPageRequest;
import com.tencent.wxcloudrun.model.MapPoint;
import com.tencent.wxcloudrun.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    
    @Autowired
    private MapService mapService;
    
    /**
     * 加载地图页面点位信息
     * 接口地址: POST /map/loadPage
     * 
     * @param request 请求参数，包含 topLeft 和 bottomRight
     * @return 统一响应格式，包含点位信息列表
     */
    @PostMapping("/loadPage")
    public ResponseResult<List<MapPoint>> loadPage(@RequestBody MapLoadPageRequest request) {
        List<MapPoint> points = mapService.loadPage(request.getTopLeft(), request.getBottomRight());
        return ResponseResult.success(points);
    }
}

