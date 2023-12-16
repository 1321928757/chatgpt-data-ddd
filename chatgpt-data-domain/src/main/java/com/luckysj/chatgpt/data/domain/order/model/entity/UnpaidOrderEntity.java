package com.luckysj.chatgpt.data.domain.order.model.entity;

import com.luckysj.chatgpt.data.domain.order.model.valobj.PayStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 未支付的有效订单实体
 * @create 2023/12/10 17:31:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnpaidOrderEntity {

    /**
     * 用户ID；微信分配的唯一ID编码
     */
    private String openid;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 订单金额
     */
    private BigDecimal totalAmount;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 支付地址；创建支付后，获得的URL地址
     */
    private String payUrl;
    /**
     * 支付状态；0-等待支付、1-支付完成、2-支付失败、3-放弃支付
     */
    private PayStatusVO payStatus;

}
