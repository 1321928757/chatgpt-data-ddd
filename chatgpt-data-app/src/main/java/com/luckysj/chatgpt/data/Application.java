package com.luckysj.chatgpt.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

// 全局启动类
@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling //启用定时任务
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
