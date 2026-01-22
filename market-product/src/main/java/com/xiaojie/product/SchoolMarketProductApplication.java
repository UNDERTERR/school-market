package com.xiaojie.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@MapperScan("com.xiaojie.product.dao")
@EnableTransactionManagement
@EnableCaching
@EnableRedisHttpSession
@EnableFeignClients(basePackages = {"com.xiaojie.product.feign"})
@SpringBootApplication
public class SchoolMarketProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolMarketProductApplication.class, args);
    }
}
