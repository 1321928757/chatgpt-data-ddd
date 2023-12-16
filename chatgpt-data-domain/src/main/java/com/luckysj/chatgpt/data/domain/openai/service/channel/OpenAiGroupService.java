package com.luckysj.chatgpt.data.domain.openai.service.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 服务组接口
 * @create 2023/12/15 17:35:37
 */
public interface OpenAiGroupService {

    void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws JsonProcessingException;

}
