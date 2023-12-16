package com.luckysj.chatgpt.data.trigger.mq;

import com.google.common.eventbus.Subscribe;
import com.luckysj.chatgpt.data.domain.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 订单支付成功监听
 * 1. 订单支付成功回调，最好是快速变更订单状态，避免超时重试次数上限后不能做业务。所以推送出MQ消息来做【发货】流程
 * 2. 因为ChatGPT项目目前规格较小，选择了轻量的技术栈，所以使用 Guava 的 EventBus 消息总线来模拟消息使用。如果你后续的场景较大，也可以替换为 RocketMQ
 * @create 2023/12/15 15:13:19
 */
@Slf4j
@Component
public class OrderPaySuccessListener {

    @Resource
    private IOrderService orderService;

    @Subscribe
    public void handleEvent(String orderId) {
        try {
            log.info("支付完成，开始发货。订单：{}", orderId);
            orderService.deliverGoods(orderId);
            log.info("支付完成，发货完成。订单：{}", orderId);
        } catch (Exception e) {
            log.error("支付完成，发货失败。订单：{}", orderId, e);
        }
    }

}
