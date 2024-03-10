package com.luckysj.chatgpt.data.domain.openai.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 模型生成类型
 * @create 2023/12/18 17:55:30
 */
@Getter
@AllArgsConstructor
public enum GenerativeModelVO {

    TEXT("TEXT","文本"),
    IMAGES("IMAGES","图片"),
    ;

    private final String code;
    private final String info;

}
