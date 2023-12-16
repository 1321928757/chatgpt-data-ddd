package com.luckysj.chatgpt.data.trigger.http.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 商品对象dto
 * @create 2023/12/10 14:24:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleProductDTO {

    /**
     * 商品ID
     */
    private Integer productId;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品描述
     */
    private String productDesc;
    /**
     * 额度次数
     */
    private Integer quota;
    /**
     * 商品价格
     */
    private BigDecimal price;

}
