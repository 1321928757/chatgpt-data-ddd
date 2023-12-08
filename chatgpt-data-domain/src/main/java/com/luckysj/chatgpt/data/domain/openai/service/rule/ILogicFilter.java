package com.luckysj.chatgpt.data.domain.openai.service.rule;

import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;

public interface ILogicFilter {

    RuleLogicEntity<ChatProcessAggregate> fileter(ChatProcessAggregate chatProcessAggregate) throws Exception;

}
