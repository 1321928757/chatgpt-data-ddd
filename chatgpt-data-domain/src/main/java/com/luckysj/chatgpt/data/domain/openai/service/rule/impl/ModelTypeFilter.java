package com.luckysj.chatgpt.data.domain.openai.service.rule.impl;

import com.luckysj.chatgpt.data.domain.openai.annotation.LogicStrategy;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.luckysj.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.luckysj.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import com.luckysj.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 可用模型校验
 * @create 2023/12/09 18:00:27
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.MODEL_TYPE)
public class ModelTypeFilter implements ILogicFilter<UserAccountQuotaEntity> {
    @Override
    public RuleLogicEntity<ChatProcessAggregate> fileter(ChatProcessAggregate chatProcessAggregate, UserAccountQuotaEntity data) throws Exception {
        List<String> allowModelTypeList = data.getAllowModelTypeList();
        String model = chatProcessAggregate.getModel();

        if(allowModelTypeList.contains(model)){
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS)
                    .data(chatProcessAggregate)
                    .build();
        }

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.REFUSE)
                .info("当前账户不支持使用 " + model + " 模型！可以联系客服升级账户。")
                .data(chatProcessAggregate)
                .build();
    }
}
