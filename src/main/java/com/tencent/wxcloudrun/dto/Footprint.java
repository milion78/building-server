package com.tencent.wxcloudrun.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Footprint implements Comparable<Footprint> {

    /**
     * 倒序：actionTime 从晚到早，图片数量从多到少，username 倒序。没有 actionTime 的排在最后。
     */
    public static final Comparator<Footprint> DESCENDING = new Comparator<Footprint>() {
        @Override
        public int compare(Footprint left, Footprint right) {
            return compareFootprint(left, right, true);
        }
    };

    /**
     * 正序：actionTime 从早到晚，图片数量从少到多，username 正序。没有 actionTime 的排在最后。
     */
    public static final Comparator<Footprint> ASCENDING = new Comparator<Footprint>() {
        @Override
        public int compare(Footprint left, Footprint right) {
            return compareFootprint(left, right, false);
        }
    };

    private String username;

    private String userhead;

    /**
     * 返回格式 yyyy-MM-dd HH:mm:ss，东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    /**
     * 自然顺序为倒序。三项都相同时返回 0，由稳定排序保持原有先后，数据不变时结果不变。
     */
    @Override
    public int compareTo(Footprint other) {
        return DESCENDING.compare(this, other);
    }

    private static int compareFootprint(Footprint left, Footprint right, boolean descending) {
        int timeOrder = compareActionTime(left.getActionTime(), right.getActionTime(), descending);
        if (timeOrder != 0) {
            return timeOrder;
        }
        int imageOrder = compareImageCount(imageCount(left), imageCount(right), descending);
        if (imageOrder != 0) {
            return imageOrder;
        }
        return compareUsername(left.getUsername(), right.getUsername(), descending);
    }

    private static int compareActionTime(Date left, Date right, boolean descending) {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        int order = left.compareTo(right);
        return descending ? -order : order;
    }

    private static int compareImageCount(int left, int right, boolean descending) {
        int order = left - right;
        return descending ? -order : order;
    }

    private static int compareUsername(String left, String right, boolean descending) {
        String leftName = left == null ? "" : left;
        String rightName = right == null ? "" : right;
        int order = leftName.compareTo(rightName);
        return descending ? -order : order;
    }

    private static int imageCount(Footprint footprint) {
        List<String> imgUrl = footprint.getImgUrl();
        return imgUrl == null ? 0 : imgUrl.size();
    }
}
