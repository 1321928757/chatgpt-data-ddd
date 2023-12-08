package com.luckysj.chatgpt.data.domain.openai.service;

import cn.bugstack.chatgpt.session.OpenAiSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.luckysj.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.luckysj.chatgpt.data.types.common.Constants;
import com.luckysj.chatgpt.data.types.exception.ChatGPTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 对话服务模板模式抽象类
 * @create 2023/12/04 19:50:27
 */
@Slf4j
public abstract class AbstractChatService implements IChatService{
    //chat会话session
    @Resource
    protected OpenAiSession openAiSession; 
    
    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) {
        try {
            // 1.请求应答
            emitter.onCompletion(() -> {
                log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
            });
            emitter.onError(throwable -> log.error("流式问答请求错误，使用模型：{}", chatProcess.getModel(), throwable));

            // 2.规则过滤(这里用到了使用次数校验和敏感词校验)
            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess
                    ,DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode());
            // 判断校验是否通过
            if(!LogicCheckTypeVO.SUCCESS.equals(ruleLogicEntity.getType())){
                emitter.send(ruleLogicEntity.getInfo());
                emitter.complete();
                return emitter;
            }

            // 3.应答处理
            this.doMessageResponse(ruleLogicEntity.getData(), emitter);
        } catch (Exception e) {
            throw new ChatGPTException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 3. 返回结果
        return emitter;

    }

    // 规则校验
    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcessAggregate, String ... logics) throws Exception;

    // 消息应当处理
    protected abstract void doMessageResponse(ChatProcessAggregate chatProcessAggregate, ResponseBodyEmitter emitter) throws JsonProcessingException;
}
