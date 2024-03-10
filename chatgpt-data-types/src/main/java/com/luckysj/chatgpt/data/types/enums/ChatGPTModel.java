package com.luckysj.chatgpt.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ChatGPTModel {

    /** gpt-3.5-turbo */
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    /** 文生图 */
    DALL_E_2("dall-e-2"),
    DALL_E_3("dall-e-3"),
    ;

    private final String code;

}
