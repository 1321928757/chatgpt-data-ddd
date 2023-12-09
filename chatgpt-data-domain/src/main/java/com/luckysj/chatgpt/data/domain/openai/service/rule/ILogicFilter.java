package com.luckysj.chatgpt.data.domain.openai.service.rule;

import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;

public interface ILogicFilter<T> {

    RuleLogicEntity<ChatProcessAggregate> fileter(ChatProcessAggregate chatProcessAggregate, T data) throws Exception;

}
