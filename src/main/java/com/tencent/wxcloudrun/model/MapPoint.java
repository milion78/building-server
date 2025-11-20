package com.tencent.wxcloudrun.model;

/**
 * 地图点位信息实体类
 * 
 * @author building-service
 */
public class MapPoint {
    
    /**
     * 点位的坐标，格式："{经度,纬度}"
     */
    private String coordinate;
    
    /**
     * 点位名称
     */
    private String name;
    
    /**
     * 点位ID
     */
    private Long id;
    
    /**
     * 点位的图片链接
     */
    private String picUrl;
    
    public MapPoint() {
    }
    
    public MapPoint(String coordinate, String name, Long id, String picUrl) {
        this.coordinate = coordinate;
        this.name = name;
        this.id = id;
        this.picUrl = picUrl;
    }
    
    public String getCoordinate() {
        return coordinate;
    }
    
    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPicUrl() {
        return picUrl;
    }
    
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}

