package com.luckysj.chatgpt.data.trigger.http.dto;

import com.luckysj.chatgpt.data.types.enums.ChatGPTModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author www.luckysj.top 刘仕杰
 * @description  gpt服务请求dto
 * @create 2023/12/04 19:29:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTRequestDTO {

    /** 默认模型 */
    private String model = ChatGPTModel.GPT_3_5_TURBO.getCode();

    /** 问题描述 */
    private List<MessageEntity> messages;

}
