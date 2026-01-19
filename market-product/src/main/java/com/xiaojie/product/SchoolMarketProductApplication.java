package com.xiaojie.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@MapperScan("com.xiaojie.product.dao")
@EnableTransactionManagement
@SpringBootApplication
public class SchoolMarketProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolMarketProductApplication.class, args);
    }
}
