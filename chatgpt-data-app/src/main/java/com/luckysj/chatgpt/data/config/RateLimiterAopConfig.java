package com.luckysj.chatgpt.data.config;

import com.luckysj.chatgpt.data.aop.RateLimiterAOP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterAopConfig {
    @Bean
    public RateLimiterAOP rateLimiterAOP(){
        return new RateLimiterAOP();
    }
}
