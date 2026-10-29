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
     * 点位 code，由文保 id 转换而来
     */
    private String id;
    
    /**
     * 点位的图片链接
     */
    private String picUrl;

    /**
     * 点位图片的云存储 fileId
     */
    private String picCloudId;

    /**
     * 文保级别
     */
    private String relicRank;

    /**
     * 文保地址
     */
    private String relicAddress;
    
    public MapPoint() {
    }
    
    public MapPoint(String coordinate, String name, String id, String picUrl) {
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
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPicUrl() {
        return picUrl;
    }
    
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicCloudId() {
        return picCloudId;
    }

    public void setPicCloudId(String picCloudId) {
        this.picCloudId = picCloudId;
    }

    public String getRelicRank() {
        return relicRank;
    }

    public void setRelicRank(String relicRank) {
        this.relicRank = relicRank;
    }

    public String getRelicAddress() {
        return relicAddress;
    }

    public void setRelicAddress(String relicAddress) {
        this.relicAddress = relicAddress;
    }
    
    @Override
    public String toString() {
        return "MapPoint{coordinate=" + coordinate + ", name=" + name + ", id=" + id + ", picUrl=" + picUrl + ", picCloudId=" + picCloudId + ", relicRank=" + relicRank + ", relicAddress=" + relicAddress + "}";
    }
}

