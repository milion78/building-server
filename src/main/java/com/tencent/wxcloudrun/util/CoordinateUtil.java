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
    
    /**
     * 生成坐标格式字符串
     * 将经度和纬度组合成 "{经度,纬度}" 格式的字符串
     * 
     * @param longitude 经度值，double 类型，范围：-180 到 180
     * @param latitude 纬度值，double 类型，范围：-90 到 90
     * @return 坐标格式字符串，格式："{经度,纬度}"，例如："{12.23,-34.34}"，如果参数超出范围返回 null
     */
    public static String formatCoordinate(double longitude, double latitude) {
        try {
            // 检查经度范围：-180 到 180
            if (longitude > 180.0 || longitude < -180.0) {
                logger.warn("经度超出范围: {}", longitude);
                return null;
            }
            
            // 检查纬度范围：-90 到 90
            if (latitude > 90.0 || latitude < -90.0) {
                logger.warn("纬度超出范围: {}", latitude);
                return null;
            }
            
            // 保留8位小数
            // 使用 BigDecimal 或 String.format 来精确控制小数位数
            String longitudeStr = String.format("%.8f", longitude);
            String latitudeStr = String.format("%.8f", latitude);
            
            // 移除末尾多余的0（可选，保持格式简洁）
            longitudeStr = removeTrailingZeros(longitudeStr);
            latitudeStr = removeTrailingZeros(latitudeStr);
            
            return "{" + longitudeStr + "," + latitudeStr + "}";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 移除字符串末尾的0和小数点（如果小数点后全是0）
     * 例如：12.34000000 -> 12.34, 12.00000000 -> 12
     */
    private static String removeTrailingZeros(String numberStr) {
        if (numberStr == null || !numberStr.contains(".")) {
            return numberStr;
        }
        
        // 移除末尾的0
        numberStr = numberStr.replaceAll("0+$", "");
        // 如果末尾是小数点，也移除
        numberStr = numberStr.replaceAll("\\.$", "");
        
        return numberStr;
    }
}

