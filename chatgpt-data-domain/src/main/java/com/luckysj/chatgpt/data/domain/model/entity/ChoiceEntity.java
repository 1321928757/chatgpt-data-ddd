package com.luckysj.chatgpt.data.domain.model.entity;

import lombok.Data;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 
 * @create 2023/12/04 19:48:27
 */
@Data
public class ChoiceEntity {

    /** stream = true 请求参数里返回的属性是 delta */
    private MessageEntity delta;
    /** stream = false 请求参数里返回的属性是 delta */
    private MessageEntity message;

}
