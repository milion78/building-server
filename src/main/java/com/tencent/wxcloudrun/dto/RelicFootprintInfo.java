package com.tencent.wxcloudrun.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 文保足迹汇总信息。
 */
public class RelicFootprintInfo {

    /**
     * 按 username 去重后的用户数量
     */
    private int count;

    /**
     * 足迹 actionTime 中出现过的年份，从大到小
     */
    private List<Integer> yearList = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Integer> getYearList() {
        return yearList;
    }

    public void setYearList(List<Integer> yearList) {
        this.yearList = yearList;
    }
}
