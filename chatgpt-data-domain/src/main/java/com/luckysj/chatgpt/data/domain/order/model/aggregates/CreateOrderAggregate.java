package com.luckysj.chatgpt.data.domain.order.model.aggregates;

import com.luckysj.chatgpt.data.domain.order.model.entity.OrderEntity;
import com.luckysj.chatgpt.data.domain.order.model.entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 下单聚合对象
 * @create 2023/12/10 17:28:42
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderAggregate {

    /** 用户ID；微信用户唯一标识 */
    private String openid;
    /** 商品 */
    private ProductEntity product;
    /** 订单 */
    private OrderEntity order;

}
