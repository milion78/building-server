package com.tencent.wxcloudrun.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 访问日志拦截器
 * 记录详细的访问日志，包括毫秒级时间、入参、出参
 * 
 * @author building-service
 */
@Component
public class AccessLogInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(AccessLogInterceptor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    private static final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> requestBodyThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<ContentCachingResponseWrapper> responseWrapperThreadLocal = new ThreadLocal<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 从 request attribute 中获取响应包装器
            Object wrapper = request.getAttribute("cachedResponseWrapper");
            if (wrapper instanceof ContentCachingResponseWrapper) {
                responseWrapperThreadLocal.set((ContentCachingResponseWrapper) wrapper);
            }
            
            // 记录开始时间
            long startTime = System.currentTimeMillis();
            startTimeThreadLocal.set(startTime);
            
            // 获取请求参数
            Map<String, Object> requestParams = new HashMap<>();
            
            // 获取 URL 参数
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues != null && paramValues.length > 0) {
                    if (paramValues.length == 1) {
                        requestParams.put(paramName, paramValues[0]);
                    } else {
                        requestParams.put(paramName, paramValues);
                    }
                }
            }
            
            // 获取请求体（仅对 POST/PUT 请求）
            String requestBody = null;
            if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
                try {
                    requestBody = getRequestBody(request);
                    requestBodyThreadLocal.set(requestBody);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // 构建请求日志（使用单行格式，避免日志系统拆分）
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("========== Access Log Start ========== | ");
            logBuilder.append("Time: ").append(dateFormat.format(new Date(startTime))).append(" | ");
            logBuilder.append("Method: ").append(request.getMethod()).append(" | ");
            logBuilder.append("URI: ").append(request.getRequestURI()).append(" | ");
            logBuilder.append("Query String: ").append(request.getQueryString() != null ? request.getQueryString() : "").append(" | ");
            
            // 记录请求参数
            if (!requestParams.isEmpty()) {
                try {
                    logBuilder.append("Request Params: ").append(objectMapper.writeValueAsString(requestParams)).append(" | ");
                } catch (Exception e) {
                    e.printStackTrace();
                    logBuilder.append("Request Params: ").append(requestParams.toString()).append(" | ");
                }
            }
            
            // 记录请求体
            if (requestBody != null && !requestBody.isEmpty()) {
                logBuilder.append("Request Body: ").append(requestBody).append(" | ");
            }
            
            // 记录请求头（可选，根据需要）
            logBuilder.append("Content-Type: ").append(request.getContentType() != null ? request.getContentType() : "").append(" | ");
            logBuilder.append("Remote Addr: ").append(request.getRemoteAddr());
            
            logger.info(logBuilder.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在视图渲染前执行，这里不需要处理
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            Long startTime = startTimeThreadLocal.get();
            if (startTime != null) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                // 构建响应日志（使用单行格式，避免日志系统拆分）
                StringBuilder logBuilder = new StringBuilder();
                logBuilder.append("Response Status: ").append(response.getStatus()).append(" | ");
                
                // 获取响应内容
                ContentCachingResponseWrapper responseWrapper = responseWrapperThreadLocal.get();
                if (responseWrapper != null) {
                    try {
                        String responseBody = responseWrapper.getContentAsString();
                        if (responseBody != null && !responseBody.isEmpty()) {
                            // 限制响应内容长度，避免日志过长
                            if (responseBody.length() > 2000) {
                                logBuilder.append("Response Body: ").append(responseBody.substring(0, 2000)).append("... (truncated) | ");
                            } else {
                                logBuilder.append("Response Body: ").append(responseBody).append(" | ");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                logBuilder.append("Duration: ").append(duration).append("ms | ");
                logBuilder.append("Time: ").append(dateFormat.format(new Date(endTime))).append(" | ");
                
                // 如果有异常，记录异常信息
                if (ex != null) {
                    logBuilder.append("Exception: ").append(ex.getClass().getName()).append(" - ").append(ex.getMessage()).append(" | ");
                }
                
                logBuilder.append("========== Access Log End ==========");
                
                logger.info(logBuilder.toString());
                
                // 清理 ThreadLocal
                startTimeThreadLocal.remove();
                requestBodyThreadLocal.remove();
                responseWrapperThreadLocal.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) {
        StringBuilder body = new StringBuilder();
        try {
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body.toString();
    }
}

