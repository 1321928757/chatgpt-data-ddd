package com.luckysj.chatgpt.data.domain.openai.model.aggregates;


import com.luckysj.chatgpt.data.domain.openai.model.entity.MessageEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.GenerativeModelVO;
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

    /** 判断当前对话用户是否在白名单内*/
    public boolean isWhiteList(String whiteListStr) {
        String[] whiteList = whiteListStr.split(Constants.SPLIT);
        for (String whiteOpenid : whiteList) {
            if (whiteOpenid.equals(openid)) return true;
        }
        return false;
    }

    /** 判断当前会话选择的模型属于哪个渠道*/
    public OpenAiChannel getChannel(){
        return OpenAiChannel.getChannel(this.model);
    }

    /** 判断当前会话选择的模型属于渠道中的哪个服务*/
    public GenerativeModelVO getGenerativeModelVO() {
        // dall模型调用图片服务，其他调用文字服务
        switch (this.model) {
            case "dall-e-2":
            case "dall-e-3":
                return GenerativeModelVO.IMAGES;
            default:
                return GenerativeModelVO.TEXT;
        }
    }
}
