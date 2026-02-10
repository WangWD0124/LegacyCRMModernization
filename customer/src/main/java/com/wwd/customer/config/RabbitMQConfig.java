package com.wwd.customer.config;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.config.RabbitMQConfig
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-02-07
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-02-07     wangwd7          v1.0.0               创建
 */
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue accountAddQueue() {
        return new Queue("account.add.queue", true);
    }

    @Bean
    public Queue accountDeductQueue() {
        return new Queue("account.deduct.queue", true);
    }
}
