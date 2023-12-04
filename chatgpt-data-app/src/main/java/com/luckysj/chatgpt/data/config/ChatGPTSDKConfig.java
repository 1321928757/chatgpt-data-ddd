package com.luckysj.chatgpt.data.config;


import cn.bugstack.chatgpt.session.OpenAiSession;
import cn.bugstack.chatgpt.session.OpenAiSessionFactory;
import cn.bugstack.chatgpt.session.defaults.DefaultOpenAiSessionFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author www.luckysj.top 刘仕杰
 * @description ChatGPTSDKConfig ChatGPTSDK会话bean声明
 * @create 2023-12-04 18：53
 */
@Configuration
public class ChatGPTSDKConfig {
    @Bean
    public OpenAiSession openAiSession(ChatGPTSDKConfigProperties properties) {
        // 1. 配置文件
        cn.bugstack.chatgpt.session.Configuration configuration = new cn.bugstack.chatgpt.session.Configuration();
        configuration.setApiHost(properties.getApiHost());
        configuration.setAuthToken(properties.getAuthToken());
        configuration.setApiKey(properties.getApiKey());

        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);

        // 3. 开启会话
        return factory.openSession();
    }
}
