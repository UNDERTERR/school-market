package com.xiaojie.cart;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;



@EnableDiscoveryClient
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.xiaojie.cart.feign")
@SpringBootApplication
public class SchoolMarketCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolMarketCartApplication.class, args);
    }
}
