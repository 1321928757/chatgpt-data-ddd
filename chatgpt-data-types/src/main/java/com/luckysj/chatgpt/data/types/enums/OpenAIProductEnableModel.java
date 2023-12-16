package com.luckysj.chatgpt.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description
 * @create 2023-09-28 20:58
 */
@Getter
@AllArgsConstructor
public enum OpenAIProductEnableModel {

    CLOSE(0, "无效，已关闭"),
    OPEN(1,"有效，使用中"),
    ;

    private final Integer code;

    private final String info;

    public static com.luckysj.chatgpt.data.types.enums.OpenAIProductEnableModel get(Integer code){
        switch (code){
            case 0:
                return com.luckysj.chatgpt.data.types.enums.OpenAIProductEnableModel.CLOSE;
            case 1:
                return com.luckysj.chatgpt.data.types.enums.OpenAIProductEnableModel.OPEN;
            default:
                return com.luckysj.chatgpt.data.types.enums.OpenAIProductEnableModel.CLOSE;
        }
    }

}
