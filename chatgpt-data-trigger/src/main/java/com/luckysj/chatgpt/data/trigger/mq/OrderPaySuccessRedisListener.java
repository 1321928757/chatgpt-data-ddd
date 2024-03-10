package com.luckysj.chatgpt.data.trigger.mq;

import com.luckysj.chatgpt.data.domain.order.service.IOrderService;
import com.luckysj.chatgpt.data.types.annotation.RedisTopic;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 支付完成后发货消息监听器
 * @create 2023/12/17 22:12:22
 */
@Slf4j
@Service()
@RedisTopic(topic = "delivery")
public class OrderPaySuccessRedisListener implements MessageListener<String> {
    @Resource
    private IOrderService orderService;

    @Override
    public void onMessage(CharSequence charSequence, String orderId) {
        try {
            log.info("支付完成，开始发货。订单：{}", orderId);
            orderService.deliverGoods(orderId);
            log.info("支付完成，发货完成。订单：{}", orderId);
        } catch (Exception e) {
            log.error("支付完成，发货失败。订单：{}", orderId, e);
        }
    }
}
