package com.luckysj.chatgpt.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 支付配置信息
 * @create 2023/12/15 11:05:20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "pay.alipay", ignoreInvalidFields = true)
public class AliPayConfigProperties {
    /** appid*/
    private String appId;
    /** 应用私钥*/
    private String app_private_key;
    /** 支付公钥*/
    private String alipay_public_key;
    /** 支付宝网关地址*/
    private String url;
    /** 二维码跳转地址*/
    private String qrcode_url;
    /** 支付结果通知地址*/
    private String notify_url;
    /** 编码*/
    public static String CHARSET = "UTF-8";
    /** 返回格式*/
    public static String FORMAT = "json";
    /** 日志记录目录*/
    public static String log_path = "/log";
    /** RSA2*/
    public static String SIGNTYPE = "RSA2";
}
