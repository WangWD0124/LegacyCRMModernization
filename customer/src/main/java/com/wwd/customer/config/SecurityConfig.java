package com.wwd.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.config.SecurityConfig
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-12-29
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-12-29     wangwd7          v1.0.0               创建
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 BCrypt 强哈希函数加密密码
        return new BCryptPasswordEncoder();
    }
}
