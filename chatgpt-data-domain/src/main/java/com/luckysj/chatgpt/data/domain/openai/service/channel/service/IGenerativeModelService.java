package com.luckysj.chatgpt.data.domain.openai.service.channel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 模型生成文字/图片服务 接口
 * @create 2023/12/18 17:48:24
 */
public interface IGenerativeModelService {
    void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception;
}
