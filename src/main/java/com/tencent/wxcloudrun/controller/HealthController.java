package com.tencent.wxcloudrun.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口控制器
 * 
 * @author building-service
 */
@RestController
public class HealthController {

    /**
     * 健康检查接口
     * 访问地址: http://127.0.0.1:8080/health
     * 
     * @return 返回 "success" 字符串，表示服务运行正常
     */
    @GetMapping("/health")
    public String health() {
        return "success";
    }
}

