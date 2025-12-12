package com.wwd.customer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.config.WebConfig
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-12-09
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-12-09     wangwd7          v1.0.0               创建
 */
// 在web模块中添加CORS配置
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("====== 配置CORS规则：允许来自 http://localhost:8081 的请求 ======"); // 添加这行
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8081") // Vue开发服务器
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
