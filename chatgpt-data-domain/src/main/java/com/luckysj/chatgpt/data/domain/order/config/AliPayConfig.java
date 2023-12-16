package com.luckysj.chatgpt.data.domain.order.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 支付宝支付配置
 * @create 2023/12/11 20:22:13
 */
@Data
@Configuration
public class AliPayConfig {
    // appid
    @Value("${pay.alipay.app_id}")
    private String APP_ID;
    // 私钥
    @Value("${pay.alipay.app_private_key}")
    private String APP_PRIVATE_KEY;
    // 公钥
    @Value("${pay.alipay.alipay_public_key}")
    private String ALIPAY_PUBLIC_KEY;
    // 请求网关地址
    @Value("${pay.alipay.url}")
    private String URL;
    // 二维码跳转地址
    @Value("${pay.alipay.qrcode_url}")
    private String QRCODE_URL;
    // 支付结果通知接口地址
    @Value("${pay.alipay.notify_url}")
    private String NOTIFY_URL;
    // 编码
    public static String CHARSET = "UTF-8";
    // 返回格式
    public static String FORMAT = "json";
    // 日志记录目录
    public static String log_path = "/log";
    // RSA2
    public static String SIGNTYPE = "RSA2";
}
