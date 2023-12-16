package com.luckysj.chatgpt.data.domain.openai.service;

import cn.bugstack.chatgpt.session.OpenAiSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.luckysj.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.luckysj.chatgpt.data.domain.openai.repository.IOpenAiRepository;
import com.luckysj.chatgpt.data.domain.openai.service.channel.OpenAiGroupService;
import com.luckysj.chatgpt.data.domain.openai.service.channel.impl.ChatGLMService;
import com.luckysj.chatgpt.data.domain.openai.service.channel.impl.ChatGPTService;
import com.luckysj.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.luckysj.chatgpt.data.types.common.Constants;
import com.luckysj.chatgpt.data.types.enums.OpenAiChannel;
import com.luckysj.chatgpt.data.types.exception.ChatGPTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 对话服务模板模式抽象类
 * @create 2023/12/04 19:50:27
 */
@Slf4j
public abstract class AbstractChatService implements IChatService {

    @Resource
    private IOpenAiRepository openAiRepository;

    // 对话服务组
    private final Map<OpenAiChannel, OpenAiGroupService> openAiGroup = new HashMap<>();

    public AbstractChatService(ChatGPTService chatGPTService, ChatGLMService chatGLMService) {
        openAiGroup.put(OpenAiChannel.ChatGPT, chatGPTService);
        openAiGroup.put(OpenAiChannel.ChatGLM, chatGLMService);
    }


    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) {
        try {
            // 1.绑定应答回调事件
            emitter.onCompletion(() -> {
                log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
            });
            emitter.onError(throwable -> log.error("流式问答请求错误，使用模型：{}", chatProcess.getModel(), throwable));

            // 2.查询账户
            UserAccountQuotaEntity userAccountQuotaEntity = openAiRepository.queryUserAccount(chatProcess.getOpenid());

            // 3.规则过滤
            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess, userAccountQuotaEntity,
                    DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode(),
                    DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.ACCOUNT_STATUS.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.MODEL_TYPE.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.USER_QUOTA.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode()
            );
            // 判断校验是否通过
            if (!LogicCheckTypeVO.SUCCESS.equals(ruleLogicEntity.getType())) {
                emitter.send(ruleLogicEntity.getInfo());
                emitter.complete();
                return emitter;
            }

            // 4.请求应答处理【使用策略模式判断使用GPT的会话还是其他模型的 会话】
            openAiGroup.get(chatProcess.getChannel()).doMessageResponse(ruleLogicEntity.getData(), emitter);

        } catch (Exception e) {
            throw new ChatGPTException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 3. 返回结果
        return emitter;

    }

    // 规则校验
    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(
            ChatProcessAggregate chatProcessAggregate, UserAccountQuotaEntity userAccountQuotaEntity,
            String... logics) throws Exception;

}
