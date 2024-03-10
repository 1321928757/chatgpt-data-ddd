package com.luckysj.chatgpt.data.domain.openai.service.channel.impl;

import cn.bugstack.chatgpt.common.Constants;
import cn.bugstack.chatgpt.domain.chat.ChatChoice;
import cn.bugstack.chatgpt.domain.chat.ChatCompletionRequest;
import cn.bugstack.chatgpt.domain.chat.ChatCompletionResponse;
import cn.bugstack.chatgpt.domain.chat.Message;
import cn.bugstack.chatgpt.session.OpenAiSession;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.GenerativeModelVO;
import com.luckysj.chatgpt.data.domain.openai.service.channel.OpenAiGroupService;
import com.luckysj.chatgpt.data.domain.openai.service.channel.service.IGenerativeModelService;
import com.luckysj.chatgpt.data.domain.openai.service.channel.service.impl.ImageGenerativeModelServiceImpl;
import com.luckysj.chatgpt.data.domain.openai.service.channel.service.impl.TextGenerativeModelServiceImpl;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author www.luckysj.top 刘仕杰
 * @description chatgpt服务
 * @create 2023/12/15 18:26:20
 */
@Service
public class ChatGPTService implements OpenAiGroupService {

    private Map<GenerativeModelVO, IGenerativeModelService> modelServiceGroup = new HashMap<>();

    // 初始化自动注入对话和图片服务
    public ChatGPTService(ImageGenerativeModelServiceImpl imageGenerativeModelService, TextGenerativeModelServiceImpl textGenerativeModelService) {
        modelServiceGroup.put(GenerativeModelVO.IMAGES, imageGenerativeModelService);
        modelServiceGroup.put(GenerativeModelVO.TEXT, textGenerativeModelService);
    }
    @Override
    public void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception {
        GenerativeModelVO generativeModelVO = chatProcess.getGenerativeModelVO();
        modelServiceGroup.get(generativeModelVO).doMessageResponse(chatProcess, emitter);
    }
}
