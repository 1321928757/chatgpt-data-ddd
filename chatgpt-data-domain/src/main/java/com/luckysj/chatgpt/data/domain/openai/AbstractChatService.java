package com.luckysj.chatgpt.data.domain.openai;

import cn.bugstack.chatgpt.session.OpenAiSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.luckysj.chatgpt.data.domain.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.types.cmmon.Constants;
import com.luckysj.chatgpt.data.types.exception.ChatGPTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 
 * @create 2023/12/04 19:50:27
 */
@Slf4j
public abstract class AbstractChatService implements IChatService{
    //chat会话session
    @Resource
    protected OpenAiSession openAiSession; 
    
    @Override
    public ResponseBodyEmitter completions(ChatProcessAggregate chatProcess) {
        // 1.权限校验
        String token = chatProcess.getToken();
        if(!token.equals("Bearer test")){
            throw new ChatGPTException(Constants.ResponseCode.TOKEN_ERROR.getCode(), Constants.ResponseCode.TOKEN_ERROR.getInfo());
        }

        // 2.请求应答
        ResponseBodyEmitter emitter = new ResponseBodyEmitter( 3 * 60 * 1000L);
        // 注册请求应答成功与失败的回调函数
        emitter.onCompletion(() -> {
            log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
        });
        emitter.onError(throwable -> log.error("流式问答请求错误，使用模型：{}", chatProcess.getModel(), throwable));

        // 3.应答处理,这里交给子类处理
        try {
            this.doMessageResponse(chatProcess, emitter);
        } catch (Exception e) {
            throw new ChatGPTException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 4. 返回结果
        return emitter;

    }

    protected abstract void doMessageResponse(ChatProcessAggregate chatProcessAggregate, ResponseBodyEmitter emitter) throws JsonProcessingException;
}
