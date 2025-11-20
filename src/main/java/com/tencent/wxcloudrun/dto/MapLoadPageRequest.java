package com.tencent.wxcloudrun.dto;

/**
 * 地图加载页面请求参数
 * 
 * @author building-service
 */
public class MapLoadPageRequest {
    
    /**
     * 地图左上角经纬度，格式："{经度,纬度}"
     */
    private String topLeft;
    
    /**
     * 地图右下角经纬度，格式："{经度,纬度}"
     */
    private String bottomRight;
    
    public MapLoadPageRequest() {
    }
    
    public MapLoadPageRequest(String topLeft, String bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }
    
    public String getTopLeft() {
        return topLeft;
    }
    
    public void setTopLeft(String topLeft) {
        this.topLeft = topLeft;
    }
    
    public String getBottomRight() {
        return bottomRight;
    }
    
    public void setBottomRight(String bottomRight) {
        this.bottomRight = bottomRight;
    }
}

