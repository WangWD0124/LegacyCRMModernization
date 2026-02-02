package com.wwd.finance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.wwd.financeapi.api") // 明确指定Feign扫描路径
@ComponentScan(basePackages = {
        "com.wwd.finance",
        "com.wwd.base",
        "com.wwd.finance.config" // 添加config包扫描
})
@MapperScan(basePackages = "com.wwd.finance.mapper") // 添加Mapper扫描
public class FinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceApplication.class, args);
    }

}
