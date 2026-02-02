package com.wwd.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableDiscoveryClient //启动Nacos服务注册与发现功能。  标记性注解可省略，spring boot自动配置开启
@EnableFeignClients //启动Feign组件扫描。  不可省略，spring不会自动扫描@FeignClient注解的接口，并为它们创建动态代理实现类
@ComponentScan({"com.wwd.common", "com.wwd.customer"})
public class CustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

}
