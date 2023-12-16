package com.luckysj.chatgpt.data.domain.openai.service;

import cn.bugstack.chatgpt.common.Constants;
import cn.bugstack.chatgpt.domain.chat.ChatChoice;
import cn.bugstack.chatgpt.domain.chat.ChatCompletionRequest;
import cn.bugstack.chatgpt.domain.chat.ChatCompletionResponse;
import cn.bugstack.chatgpt.domain.chat.Message;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.luckysj.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.luckysj.chatgpt.data.domain.openai.service.channel.impl.ChatGLMService;
import com.luckysj.chatgpt.data.domain.openai.service.channel.impl.ChatGPTService;
import com.luckysj.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.luckysj.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService extends AbstractChatService{

    // 校验规则工厂
    @Resource
    private DefaultLogicFactory logicFactory;

    public ChatService(ChatGPTService chatGPTService, ChatGLMService chatGLMService) {
        super(chatGPTService, chatGLMService);
    }

    @Override
    protected RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, UserAccountQuotaEntity userAccountQuotaEntity, String... logics) throws Exception {
        // 获取到校验规则map
        Map<String, ILogicFilter> logicFilterMap = logicFactory.openLogicFilter();
        // 调用对应校验规则进行校验
        RuleLogicEntity<ChatProcessAggregate> entity = null;
        for (String code : logics) {
            // 传入规则为空，不需要校验
            if(DefaultLogicFactory.LogicModel.NULL.getCode().equals(code)) continue;
            // 校验
            entity = logicFilterMap.get(code).fileter(chatProcess, userAccountQuotaEntity);
            if (!LogicCheckTypeVO.SUCCESS.equals(entity.getType())){
                return entity;
            }
        }

        // entity 为 null的情况一般为传入的logics校验规则参数为空，即没有使用任何校验规则，这样也算校验通过
        return entity != null ? entity : RuleLogicEntity.<ChatProcessAggregate>builder()
                .data(chatProcess).type(LogicCheckTypeVO.SUCCESS).build();
    }
}
