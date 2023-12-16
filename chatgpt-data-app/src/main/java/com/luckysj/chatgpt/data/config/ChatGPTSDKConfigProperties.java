package com.luckysj.chatgpt.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author www.luckysj.top 刘仕杰
 * @description ChatGPT会话工厂配置
 * @create 2023-12-04 18：53
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chatgpt.sdk.config", ignoreInvalidFields = true)
public class ChatGPTSDKConfigProperties {
    /** openai 代理地址 */
    private String apiHost;
    /** apikey，*/
    private String apiKey;
    /** 验证tokne（这里暂时用不到，openai不需要，如果需要中转服务进行会话前认证可以使用该字段） */
    private String authToken;
}
