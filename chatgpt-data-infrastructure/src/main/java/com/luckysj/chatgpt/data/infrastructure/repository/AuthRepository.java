package com.luckysj.chatgpt.data.infrastructure.repository;

import com.luckysj.chatgpt.data.domain.auth.repository.IAuthRepository;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.UserAccountStatusVO;
import com.luckysj.chatgpt.data.infrastructure.dao.IUserAccountDao;
import com.luckysj.chatgpt.data.infrastructure.po.UserAccountPO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthRepository implements IAuthRepository {
    @Resource
    private IUserAccountDao userAccountDao;
    @Value("${app.config.new-user-quota}")
    private Integer initialQuota; //新用户默认额度
    @Value("${app.config.new-user-model}")
    private String initialModel; //新用户默认模型
    
    @Override
    public boolean insertUserIfNotExist(String openid) {
        // 首先判断是否已存在
        UserAccountPO userAccount = userAccountDao.queryUserAccount(openid);
        if(userAccount != null) return true;

        // 插入账号信息
        UserAccountPO  userAccountPO = new UserAccountPO();
        userAccountPO.setOpenid(openid);
        userAccountPO.setTotalQuota(initialQuota);
        userAccountPO.setSurplusQuota(initialQuota);
        userAccountPO.setModelTypes(initialModel);
        userAccountPO.setStatus(UserAccountStatusVO.AVAILABLE.getCode());
        return userAccountDao.insert(userAccountPO) > 0 ? true : false;
    }

}
