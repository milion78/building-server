package com.tencent.wxcloudrun.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 坐标工具类
 * 用于处理坐标字符串的解析和提取
 * 
 * @author building-service
 */
public class CoordinateUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(CoordinateUtil.class);
    
    /**
     * 从坐标字符串中提取经度（第一个数字）
     * 坐标格式："{经度,纬度}"，例如："{12.23,-234.34}"
     * 
     * @param coordinate 坐标字符串，格式："{经度,纬度}"
     * @return 经度值，Double 类型，如果格式错误返回 null
     */
    public static Double getLongitude(String coordinate) {
        try {
            if (coordinate == null || coordinate.trim().isEmpty()) {
                logger.warn("坐标字符串为空，无法提取经度");
                return null;
            }
            
            // 移除大括号和空格
            String cleaned = coordinate.trim();
            if (cleaned.startsWith("{")) {
                cleaned = cleaned.substring(1);
            }
            if (cleaned.endsWith("}")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
            cleaned = cleaned.trim();
            
            // 按逗号分割
            String[] parts = cleaned.split(",");
            if (parts.length < 1) {
                logger.warn("坐标字符串格式不正确，无法找到经度值: {}", coordinate);
                return null;
            }
            
            // 提取第一个数字（经度）
            String longitudeStr = parts[0].trim();
            return Double.parseDouble(longitudeStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 从坐标字符串中提取纬度（第二个数字）
     * 坐标格式："{经度,纬度}"，例如："{12.23,-234.34}"
     * 
     * @param coordinate 坐标字符串，格式："{经度,纬度}"
     * @return 纬度值，Double 类型，如果格式错误返回 null
     */
    public static Double getLatitude(String coordinate) {
        try {
            if (coordinate == null || coordinate.trim().isEmpty()) {
                logger.warn("坐标字符串为空，无法提取纬度");
                return null;
            }
            
            // 移除大括号和空格
            String cleaned = coordinate.trim();
            if (cleaned.startsWith("{")) {
                cleaned = cleaned.substring(1);
            }
            if (cleaned.endsWith("}")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
            cleaned = cleaned.trim();
            
            // 按逗号分割
            String[] parts = cleaned.split(",");
            if (parts.length < 2) {
                logger.warn("坐标字符串格式不正确，无法找到纬度值: {}", coordinate);
                return null;
            }
            
            // 提取第二个数字（纬度）
            String latitudeStr = parts[1].trim();
            return Double.parseDouble(latitudeStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

