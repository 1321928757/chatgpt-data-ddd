package com.luckysj.chatgpt.data.infrastructure.repository;

import com.luckysj.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.UserAccountStatusVO;
import com.luckysj.chatgpt.data.domain.openai.repository.IOpenAiRepository;
import com.luckysj.chatgpt.data.infrastructure.dao.IUserAccountDao;
import com.luckysj.chatgpt.data.infrastructure.po.UserAccountPO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author www.luckysj.top 刘仕杰
 * @description openai仓储服务
 * @create 2023/12/09 17:05:34
 */
@Repository
public class OpenAiRepository implements IOpenAiRepository {
    @Resource
    private IUserAccountDao userAccountDao;

    @Override
    public int subAccountQuota(String openai) {
        return userAccountDao.subAccountQuota(openai);
    }

    @Override
    public UserAccountQuotaEntity queryUserAccount(String openid) {
        UserAccountPO userAccountPO = userAccountDao.queryUserAccount(openid);
        if(userAccountPO == null) return null;

        UserAccountQuotaEntity userAccountQuotaEntity = new UserAccountQuotaEntity();
        userAccountQuotaEntity.setOpenid(userAccountPO.getOpenid());
        userAccountQuotaEntity.setTotalQuota(userAccountPO.getTotalQuota());
        userAccountQuotaEntity.setSurplusQuota(userAccountPO.getSurplusQuota());
        userAccountQuotaEntity.transModelTypes(userAccountPO.getModelTypes());
        userAccountQuotaEntity.setUserAccountStatusVO(UserAccountStatusVO.get(userAccountPO.getStatus()));
        return userAccountQuotaEntity;
    }


}
