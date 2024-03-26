package com.luckysj.chatgpt.data.trigger.job;

import com.google.common.eventbus.EventBus;
import com.luckysj.chatgpt.data.domain.order.service.IOrderService;
import com.luckysj.chatgpt.data.types.common.Constants;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 订单补货任务
 * @create 2023/12/15 15:22:10
 */
@Slf4j
@Component()
public class OrderReplenishmentJob {

    @Resource
    private IOrderService orderService;
    // @Resource
    // private EventBus eventBus;

    // @Resource(name = "delivery")
    // private RTopic redisTopic;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 执行订单补货，超时3分钟，已支付，待发货未发货的订单
     */
    @Timed(value="order_replenish_job",description="定时任务，订单补货检查")
    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        try {
            List<String> orderIds = orderService.queryReplenishmentOrder();
            if (orderIds.isEmpty()) {
                log.info("定时任务，补货补偿，不存在待发货的订单");
                return;
            }
            for (String orderId : orderIds) {
                log.info("定时任务，补货补偿，订单补货开始。orderId: {}", orderId);
                // eventBus.post(orderId);
                // redisTopic.publish(orderId);
                // rabbitTemplate.convertAndSend(Constants.MessageQueueKey.DeliveryExchange, Constants.MessageQueueKey.DeliveryKey, orderId);
                orderService.publishDeliveryMessage(orderId); //发布发货消息
            }

        } catch (Exception e) {
            log.error("定时任务，订单补货失败。", e);
        }
    }

}
