package com.luckysj.chatgpt.data.domain.openai;

import com.luckysj.chatgpt.data.domain.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface IChatService {
    ResponseBodyEmitter completions(ChatProcessAggregate chatProcess);
}
