package com.xiaojie.third;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MarketThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketThirdPartyApplication.class, args);
    }

}
