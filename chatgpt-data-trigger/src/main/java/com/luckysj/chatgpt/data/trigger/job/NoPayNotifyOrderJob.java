package com.luckysj.chatgpt.data.trigger.job;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.common.eventbus.EventBus;

import com.luckysj.chatgpt.data.domain.order.model.valobj.AliPayTradeTypeVo;
import com.luckysj.chatgpt.data.domain.order.service.IOrderService;
import com.luckysj.chatgpt.data.types.common.Constants;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 检测未接收到或未正确处理的支付回调通知
 * @create 2023/12/15 15:21:54
 */
@Slf4j
@Component()
public class NoPayNotifyOrderJob {

    @Resource
    private IOrderService orderService;

    // @Resource
    // private EventBus eventBus;

    // @Resource(name = "delivery")
    // private RTopic redisTopic;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private AlipayClient alipayClient;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Timed(value="no_pay_notify_order_job",description="定时任务，订单支付状态更新")
    @Scheduled(cron = "0 0/3 * * * ?") //三分钟执行一次
    public void exec() {
        try {
            List<String> orderIds = orderService.queryNoPayNotifyOrder();
            if (orderIds.isEmpty()) {
                log.info("定时任务，订单支付状态更新，暂无未更新订单 orderId is null");
                return;
            }
            for (String orderId : orderIds) {
                // 查询结果
                AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                JSONObject bizContent = new JSONObject();
                bizContent.put("out_trade_no", orderId);
                request.setBizContent(bizContent.toString());
                AlipayTradeQueryResponse response = alipayClient.execute(request);
                if (response.isSuccess()) {
                    String resultJson = response.getBody();
                    //转map
                    Map resultMap = JSON.parseObject(resultJson, Map.class);
                    Map alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
                    //支付结果
                    String trade_status = (String) alipay_trade_query_response.get("trade_status");
                    log.info("定时任务，查询支付结果查询成功，支付状态为{}，orderId is {}", trade_status, orderId);

                    if(AliPayTradeTypeVo.TRADE_SUCCESS.getCode().equals(trade_status)){
                        String transactionId =response.getTradeNo();
                        String totalAmountStr = response.getTotalAmount();
                        BigDecimal totalAmount = new BigDecimal(totalAmountStr);
                        Date successTime = response.getSendPayDate();
                        boolean isSuccess = orderService.changeOrderPaySuccess(orderId, transactionId, totalAmount, successTime);
                        if (isSuccess) {
                            // 发布消息
                            // eventBus.post(orderId);
                            // redisTopic.publish(orderId);
                            // rabbitTemplate.convertAndSend(Constants.MessageQueueKey.DeliveryExchange, Constants.MessageQueueKey.DeliveryKey, orderId);
                            orderService.publishDeliveryMessage(orderId); //发布发货消息
                        }
                    }
                } else {
                    log.info("定时任务，查询支付结果失败，失败原因：{}，orderId is {}",response.getSubMsg(), orderId);
                    continue;
                }

            }
        } catch (Exception e) {
            log.error("定时任务，订单支付状态更新失败", e);
        }
    }

}
