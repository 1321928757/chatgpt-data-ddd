package com.luckysj.chatgpt.data.trigger.mq;

import com.luckysj.chatgpt.data.domain.order.service.IOrderService;
import com.luckysj.chatgpt.data.types.common.Constants;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 支付完成后发货消息监听器（rabbitmq）
 * @create 2023/12/17 22:12:22
 */
@Slf4j
@Component
public class OrderPaySuccessMqListener {
    @Resource
    private IOrderService orderService;

    // ackMode指定为手动提交模式
    @RabbitListener(queuesToDeclare = @Queue("chatgpt.quate.delivery.queue"), ackMode="MANUAL")
    public void stockUpdateHandler(String orderId , Message message, Channel channel) {
        //  如果手动ACK,消息会被监听消费,但是消息在队列中依旧存在,如果 未配置 acknowledge-mode 默认是会在消费完毕后自动ACK掉
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("发货消息开始消费，订单ID：{}", orderId);
            // 消费消息
            orderService.deliverGoods(orderId);

            // 通知 MQ 消息已被成功消费,可以ACK了
            channel.basicAck(deliveryTag, false);
            log.info("发货消息消费完成，订单ID：{}",orderId);
        } catch (IOException e) {
            try {
                // 处理失败,重新压入MQ
                channel.basicRecover();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
