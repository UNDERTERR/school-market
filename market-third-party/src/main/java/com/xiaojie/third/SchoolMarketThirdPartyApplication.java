package com.xiaojie.third;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SchoolMarketThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolMarketThirdPartyApplication.class, args);
    }

}
