package com.xiaojie.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
//import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;




@EnableFeignClients(basePackages = "com.xiaojie.ware.feign")
@EnableTransactionManagement
@EnableDiscoveryClient
//@SpringBootApplication(exclude = {GlobalTransactionAutoConfiguration.class})
@SpringBootApplication
public class SchoolMarketWareApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolMarketWareApplication.class, args);
    }
}
