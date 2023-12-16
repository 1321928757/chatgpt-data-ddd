package com.luckysj.chatgpt.data.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 支付类型
 * @create 2023/12/10 17:31:28
 */
@Getter
@AllArgsConstructor
public enum PayTypeVO {

    ZHIFUBAO_SHAHE(0, "支付宝支付"),
            ;

    private final Integer code;
    private final String desc;

    public static PayTypeVO get(Integer code){
        switch (code){
            case 0:
                return PayTypeVO.ZHIFUBAO_SHAHE;
            default:
                return PayTypeVO.ZHIFUBAO_SHAHE;
        }
    }

}
