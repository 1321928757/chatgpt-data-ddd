package com.luckysj.chatgpt.data.domain.model.aggregates;


import com.luckysj.chatgpt.data.domain.model.entity.MessageEntity;
import com.luckysj.chatgpt.data.types.enums.ChatGPTModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 聊天信息聚合对象
 * @create 2023/12/04 19:48:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatProcessAggregate {

    /** 验证信息 */
    private String token;
    /** 默认模型 */
    private String model = ChatGPTModel.GPT_3_5_TURBO.getCode();
    /** 问题描述 */
    private List<MessageEntity> messages;



}
