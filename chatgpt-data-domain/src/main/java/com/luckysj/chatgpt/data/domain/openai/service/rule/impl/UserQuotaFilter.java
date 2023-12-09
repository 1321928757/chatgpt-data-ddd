package com.luckysj.chatgpt.data.domain.openai.service.rule.impl;

import com.luckysj.chatgpt.data.domain.openai.annotation.LogicStrategy;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.luckysj.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.luckysj.chatgpt.data.domain.openai.repository.IOpenAiRepository;
import com.luckysj.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.luckysj.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 账户额度校验
 * @create 2023/12/09 17:59:33
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.USER_QUOTA)
public class UserQuotaFilter implements ILogicFilter<UserAccountQuotaEntity> {

    @Resource
    private IOpenAiRepository openAiRepository;

    @Override
    public RuleLogicEntity<ChatProcessAggregate> fileter(ChatProcessAggregate chatProcessAggregate, UserAccountQuotaEntity data) throws Exception {
        if (data.getSurplusQuota() > 0) {
            // 扣减账户额度；因为是个人账户数据，无资源竞争，所以直接使用数据库也可以。但为了效率，也可以优化为 Redis 扣减。
            int updateCount = openAiRepository.subAccountQuota(data.getOpenid());
            if (0 != updateCount) {
                return RuleLogicEntity.<ChatProcessAggregate>builder()
                        .type(LogicCheckTypeVO.SUCCESS).data(chatProcessAggregate).build();
            }

            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .info("个人账户，总额度【" + data.getTotalQuota() + "】次，已耗尽！")
                    .type(LogicCheckTypeVO.REFUSE).data(chatProcessAggregate).build();
        }

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .info("个人账户，总额度【" + data.getTotalQuota() + "】次，已耗尽！")
                .type(LogicCheckTypeVO.REFUSE).data(chatProcessAggregate).build();
    }
}
