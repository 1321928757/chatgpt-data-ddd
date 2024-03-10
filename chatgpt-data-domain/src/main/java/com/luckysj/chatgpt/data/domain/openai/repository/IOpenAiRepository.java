package com.luckysj.chatgpt.data.domain.openai.repository;

import com.luckysj.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;

/**
 * @author www.luckysj.top 刘仕杰
 * @description openai仓储接口
 * @create 2023/12/09 17:10:50
 */
public interface IOpenAiRepository {

    int subAccountQuota(String openai);

    UserAccountQuotaEntity queryUserAccount(String openid);

    void putRedisVisitCount(String key, Integer value, Long time);

    int getRedisVisitCount(String key);

}
