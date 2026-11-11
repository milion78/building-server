package com.tencent.wxcloudrun.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Footprint {

    private String username;

    private String userhead;

    private Date actionTime;

    /**
     * 是否在现场。1 表示在现场，0 表示不在。
     */
    private Integer onSite = 0;

    private List<String> imgUrl = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserhead() {
        return userhead;
    }

    public void setUserhead(String userhead) {
        this.userhead = userhead;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public Integer getOnSite() {
        return onSite;
    }

    public void setOnSite(Integer onSite) {
        this.onSite = onSite;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }
}
