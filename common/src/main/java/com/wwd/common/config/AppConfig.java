package com.wwd.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.config.AppConfig
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-11-20
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-11-20     wangwd7          v1.0.0               创建
 */
@Configuration
@PropertySource("classpath:application.yml")
public class AppConfig {

    @Value("${app.thread-pool.size:10}")
    private int threadPoolSize;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
