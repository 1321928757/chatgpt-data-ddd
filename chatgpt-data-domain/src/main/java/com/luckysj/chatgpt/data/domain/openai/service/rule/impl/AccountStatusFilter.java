package com.luckysj.chatgpt.data.domain.openai.service.rule.impl;

import com.luckysj.chatgpt.data.domain.openai.annotation.LogicStrategy;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.luckysj.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.UserAccountStatusVO;
import com.luckysj.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.luckysj.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 账户状态校验
 * @create 2023/12/09 18:00:10
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.ACCOUNT_STATUS)
public class AccountStatusFilter implements ILogicFilter<UserAccountQuotaEntity> {
    @Override
    public RuleLogicEntity<ChatProcessAggregate> fileter(ChatProcessAggregate chatProcessAggregate, UserAccountQuotaEntity userAccount) throws Exception {
        // 首先判断账户是否存在
        if(userAccount == null){
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.REFUSE)
                    .data(chatProcessAggregate)
                    .info("当前账户信息不存在，暂时不可使用，请尝试重新登录，或者联系网站作者QQ1321928757")
                    .build();
        }

        // 判断账户状态是否为可用状态
        if(UserAccountStatusVO.AVAILABLE.equals(userAccount.getUserAccountStatusVO())){
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcessAggregate)
                    .build();
        }

        // 校验通过
        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .info("您的账户已冻结，暂时不可使用。如果有疑问，可以联系客户解冻账户。")
                .type(LogicCheckTypeVO.REFUSE)
                .data(chatProcessAggregate)
                .build();
    }
}
