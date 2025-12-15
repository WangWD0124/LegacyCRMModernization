package com.wwd.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.nio.charset.Charset;
import java.util.Properties;


@SpringBootApplication
@EnableDiscoveryClient //启动Nacos服务注册与发现功能。  标记性注解可省略，spring boot自动配置开启
public class CustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

}
