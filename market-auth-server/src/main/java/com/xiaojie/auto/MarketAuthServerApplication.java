package com.xiaojie.auto;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.xiaojie.auto.feign")
@SpringBootApplication
public class MarketAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketAuthServerApplication.class, args);
    }

}
