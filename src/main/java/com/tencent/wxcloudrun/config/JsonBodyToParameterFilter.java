package com.tencent.wxcloudrun.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * JSON 请求体转参数过滤器
 * 将 JSON 请求体中的参数提取到 request parameter 中，使 @RequestParam 能够读取
 * 
 * @author building-service
 */
@Component
@Order(0)  // 在 ResponseCachingFilter 之前执行
public class JsonBodyToParameterFilter implements Filter {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                
                // 只处理 POST/PUT 请求，且 Content-Type 为 application/json
                String contentType = httpRequest.getContentType();
                if (contentType != null && contentType.contains("application/json") 
                        && ("POST".equalsIgnoreCase(httpRequest.getMethod()) 
                            || "PUT".equalsIgnoreCase(httpRequest.getMethod()))) {
                    
                    // 读取请求体
                    String requestBody = getRequestBody(httpRequest);
                    
                    if (requestBody != null && !requestBody.trim().isEmpty()) {
                        try {
                            // 解析 JSON
                            @SuppressWarnings("unchecked")
                            Map<String, Object> jsonMap = objectMapper.readValue(requestBody, Map.class);
                            
                            // 创建参数包装器
                            ParameterRequestWrapper wrappedRequest = new ParameterRequestWrapper(httpRequest);
                            
                            // 将 JSON 中的参数添加到 request parameter 中
                            for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                                Object value = entry.getValue();
                                if (value != null) {
                                    wrappedRequest.addParameter(entry.getKey(), value.toString());
                                }
                            }
                            
                            // 继续过滤器链，使用包装后的请求
                            chain.doFilter(wrappedRequest, response);
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            // JSON 解析失败，继续使用原始请求
                        }
                    }
                }
            }
            
            // 不是 JSON 请求或解析失败，继续使用原始请求
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            chain.doFilter(request, response);
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

