package com.luckysj.chatgpt.data.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 支付宝支付配置
 * @create 2023/12/15 11:04:06
 */
@Configuration
public class AliPayClientConfig {

    /**
    * @description 支付宝请求客户端
    * @param aliPayConfig 支付配置
    * @date 2023/12/15 11:22:14
    */
    @Bean
    public AlipayClient alipayClient(AliPayConfigProperties aliPayConfig){
        return new DefaultAlipayClient(aliPayConfig.getUrl(), aliPayConfig.getAppId(),
                aliPayConfig.getApp_private_key(), aliPayConfig.FORMAT, aliPayConfig.CHARSET,
                aliPayConfig.getAlipay_public_key(), aliPayConfig.SIGNTYPE);
    }

}
