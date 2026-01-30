package com.xiaojie.cart.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 购物车服务测试配置注解
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6370",
    "spring.redis.database=1",
    "spring.session.store-type=redis",
    "logging.level.com.xiaojie=DEBUG"
})
public @interface CartIntegrationTest {
}