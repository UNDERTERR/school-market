package com.xiaojie.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients
@EnableTransactionManagement
@EnableDiscoveryClient
@SpringBootApplication(exclude = {GlobalTransactionAutoConfigurcation.class})
public class SchoolMarketWareApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolMarketWareApplication.class, args);
    }
}
