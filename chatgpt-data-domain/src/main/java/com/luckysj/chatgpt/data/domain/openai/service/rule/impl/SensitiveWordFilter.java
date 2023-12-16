package com.luckysj.chatgpt.data.domain.openai.service.rule.impl;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.luckysj.chatgpt.data.domain.openai.annotation.LogicStrategy;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.MessageEntity;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.luckysj.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.luckysj.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.luckysj.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 敏感词校验规则
 * @create 2023/12/08 17:02:36
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.SENSITIVE_WORD)
public class SensitiveWordFilter implements ILogicFilter<UserAccountQuotaEntity> {
    // 敏感词的引导类
    @Resource
    private SensitiveWordBs words;

    // 访问白名单
    @Value("${app.config.white-list}")
    private String whiteListStr;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> fileter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity userData) throws Exception {
        // 白名单用户不做限制
        if(chatProcess.isWhiteList(whiteListStr)){
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
        }

        ChatProcessAggregate newChatProcessAggregate = new ChatProcessAggregate();
        newChatProcessAggregate.setOpenid(chatProcess.getOpenid());
        newChatProcessAggregate.setModel(chatProcess.getModel());

        List<MessageEntity> newMessages = chatProcess.getMessages().stream()
                .map(message -> {
                    String content = message.getContent();
                    // 替换敏感词
                    String replace = words.replace(content);
                    return MessageEntity.builder()
                            .role(message.getRole())
                            .name(message.getName())
                            .content(replace)
                            .build();
                })
                .collect(Collectors.toList());

        newChatProcessAggregate.setMessages(newMessages);

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.SUCCESS)
                .data(newChatProcessAggregate)
                .build();
    }
}
