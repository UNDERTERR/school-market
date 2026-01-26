package com.xiaojie.auto.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Session配置
 */
@Configuration
public class SessionConfig {

    /**
     * Session Redis序列化配置
     * 使用JSON序列化，便于调试和跨语言支持
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    /**
     * Session Cookie配置
     * 针对school-market项目调整
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        
        // Cookie名称改为school-market相关
        serializer.setCookieName("MARKETSESSIONID");
        
        // 域名配置 - 根据你的实际域名调整
        // 如果是本地开发，可以注释掉这行
        // serializer.setDomainName("school-market.com");
        
        // Cookie路径
        serializer.setCookiePath("/");
        
        // 是否使用HTTP Only（安全考虑）
        serializer.setUseHttpOnlyCookie(true);
        
        // 是否使用Secure Cookie（HTTPS时启用）
        // serializer.setUseSecureCookie(true);
        
        return serializer;
    }
}