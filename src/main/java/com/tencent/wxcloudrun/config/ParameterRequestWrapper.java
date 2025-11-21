package com.tencent.wxcloudrun.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数请求包装器
 * 用于在请求中添加额外的参数，使 @RequestParam 能够读取
 * 
 * @author building-service
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {
    
    private Map<String, String[]> params = new HashMap<>();
    
    public ParameterRequestWrapper(HttpServletRequest request) {
        super(request);
        // 复制原有的参数
        params.putAll(request.getParameterMap());
    }
    
    /**
     * 添加参数
     */
    public void addParameter(String name, String value) {
        if (value != null) {
            params.put(name, new String[]{value});
        }
    }
    
    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }
    
    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }
    
    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }
}

