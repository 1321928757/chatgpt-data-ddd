package com.luckysj.chatgpt.data.domain.weixin.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 微信公众号用户消息行为特征码
 * @create 2023/12/17 21:17:38
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ContentCodeVO {
    GENCODE("403","获取验证码"),
    GETOPENID("404","获取当前账户OpenId");

    private String code;
    private String desc;
}
