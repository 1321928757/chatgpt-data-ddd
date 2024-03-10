package com.luckysj.chatgpt.data.domain.openai.service.rule.impl;

import com.google.common.cache.Cache;
import com.luckysj.chatgpt.data.domain.openai.annotation.LogicStrategy;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.luckysj.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.luckysj.chatgpt.data.domain.openai.repository.IOpenAiRepository;
import com.luckysj.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.luckysj.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 访问总次数校验规则
 * @create 2023/12/08 17:02:36
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.ACCESS_LIMIT)
public class AccessLimitFilter implements ILogicFilter<UserAccountQuotaEntity> {

    // 访问限制次数
    @Value("${app.config.limit-count}")
    private Integer limitCount;

    // 访问时间周期
    @Value("${app.config.limit-count-time}")
    private Long limitCountTime;

    // 访问白名单
    @Value("${app.config.white-list}")
    private String whiteListStr;

    @Resource
    private IOpenAiRepository openAiRepository;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> fileter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity userData) throws Exception {
        // 白名单用户不做限制
        if(chatProcess.isWhiteList(whiteListStr)){
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcess)
                    .build();
        }

        String openid = chatProcess.getOpenid();
        // 判断在时间周期内的访问次数
        int visitCount = openAiRepository.getRedisVisitCount(openid);
        if (visitCount < limitCount) {
            openAiRepository.putRedisVisitCount(openid, visitCount + 1, limitCountTime *  60 * 1000);
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
        }

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .info("账户在" + limitCountTime + "分钟内访问次数超过" + limitCount + "次,访问过于频繁,请稍后重试！")
                .type(LogicCheckTypeVO.REFUSE).data(chatProcess).build();
    }
}
