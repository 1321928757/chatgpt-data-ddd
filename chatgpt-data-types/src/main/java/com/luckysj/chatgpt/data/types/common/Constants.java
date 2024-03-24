package com.luckysj.chatgpt.data.types.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Constants {
    // 白名单分隔符
    public final static String SPLIT = ",";

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum ResponseCode {
        SUCCESS("0000", "成功"),
        UN_ERROR("0001", "未知错误"),
        ILLEGAL_PARAMETER("0002", "非法参数"),
        TOKEN_ERROR("0003", "权限拦截"),
        ORDER_PRODUCT_ERR("OE001", "所购商品已下线，请重新选择下单商品"),
        FREQUENCY_LIMITED("O004", "访问频率过高，请稍后重试");

        private String code;
        private String info;

    }

    public static class MessageQueueKey {
        public static String DeliveryExchange = "chatgpt.quate.delivery.exchange";
        public static String DeliveryQueue = "chatgpt.quate.delivery.queue";
        public static String DeliveryKey = "chatgpt.quate.deliverye.key";
    }

}
