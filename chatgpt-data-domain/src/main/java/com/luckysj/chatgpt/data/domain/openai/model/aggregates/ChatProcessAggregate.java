package com.luckysj.chatgpt.data.domain.openai.model.aggregates;


import com.luckysj.chatgpt.data.domain.openai.model.entity.MessageEntity;
import com.luckysj.chatgpt.data.types.common.Constants;
import com.luckysj.chatgpt.data.types.enums.ChatGPTModel;
import com.luckysj.chatgpt.data.types.enums.OpenAiChannel;
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

    /** 用户ID */
    private String openid;
    /** 默认模型 */
    private String model = ChatGPTModel.GPT_3_5_TURBO.getCode();
    /** 问题描述 */
    private List<MessageEntity> messages;

    public boolean isWhiteList(String whiteListStr) {
        String[] whiteList = whiteListStr.split(Constants.SPLIT);
        for (String whiteOpenid : whiteList) {
            if (whiteOpenid.equals(openid)) return true;
        }
        return false;
    }

    public OpenAiChannel getChannel(){
        return OpenAiChannel.getChannel(this.model);
    }

}
