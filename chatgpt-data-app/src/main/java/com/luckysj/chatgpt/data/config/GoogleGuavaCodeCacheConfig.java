package com.luckysj.chatgpt.data.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.EventBus;
import com.luckysj.chatgpt.data.trigger.mq.OrderPaySuccessListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 声明Google Guava库代码缓存bean
 * @create 2023/12/05 11:13:30
 */
// @Configuration
public class GoogleGuavaCodeCacheConfig {
    @Value("${app.config.limit-count-time}")
    private Integer limitCountTime;

    // 存放验证码等不常变化的一些数据
    @Bean(name = "codeCache")
    public Cache<String, String> codeCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .build();
    }

    // 存放访问次数这种高频变化的数据
    @Bean(name = "visitCache")
    public Cache<String, Integer> visitCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(limitCountTime, TimeUnit.HOURS)
                .build();
    }

    // 事件总线，发布订阅消息（适用于一些简单场景，后续可以换成mq消息队列）
    @Bean
    public EventBus eventBusListener(OrderPaySuccessListener listener){
        EventBus eventBus = new EventBus();
        eventBus.register(listener);
        return eventBus;
    }
}
