package com.tencent.wxcloudrun.config;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 响应缓存过滤器
 * 用于包装响应，以便拦截器可以读取响应内容
 * 
 * @author building-service
 */
@Component
@Order(1)
public class ResponseCachingFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);
                
                // 将包装后的响应存储到 request attribute，供拦截器使用
                httpRequest.setAttribute("cachedResponseWrapper", wrappedResponse);
                
                chain.doFilter(request, wrappedResponse);
                
                // 将缓存的响应内容写回原始响应
                byte[] content = wrappedResponse.getContentAsBytes();
                if (content.length > 0 && !httpResponse.isCommitted()) {
                    httpResponse.getOutputStream().write(content);
                    httpResponse.getOutputStream().flush();
                }
            } else {
                chain.doFilter(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            chain.doFilter(request, response);
        }
    }
}

