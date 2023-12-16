package com.luckysj.chatgpt.data.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 支付通知结果交易状态，默认通知只有TRADE_SUCCESS会触发
 * @create 2023/12/15 12:30:19
 */
@Getter
@AllArgsConstructor
public enum AliPayTradeTypeVo {
    WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建，等待买家付款。"),
    TRADE_CLOSED("TRADE_CLOSED","未付款交易超时关闭，或支付完成后全额退款。"),
    TRADE_SUCCESS("TRADE_SUCCESS","交易支付成功。"),
    TRADE_FINISHED("TRADE_FINISHED","交易结束，不可退款。"),
    ;

    private final String code;
    private final String desc;
}
