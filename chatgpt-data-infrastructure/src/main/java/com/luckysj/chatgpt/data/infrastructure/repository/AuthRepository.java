package com.luckysj.chatgpt.data.infrastructure.repository;

import com.luckysj.chatgpt.data.domain.auth.repository.IAuthRepository;
import com.luckysj.chatgpt.data.domain.openai.model.valobj.UserAccountStatusVO;
import com.luckysj.chatgpt.data.infrastructure.dao.IUserAccountDao;
import com.luckysj.chatgpt.data.infrastructure.po.UserAccountPO;
import com.luckysj.chatgpt.data.infrastructure.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class AuthRepository implements IAuthRepository {
    @Resource
    private IUserAccountDao userAccountDao;
    @Resource
    private IRedisService redisService;
    @Value("${app.config.new-user-quota}")
    private Integer initialQuota; //新用户默认额度
    @Value("${app.config.new-user-model}")
    private String initialModel; //新用户默认模型

    // 验证码前缀
    private static final String Key = "weixin_code:";

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

    @Override
    public String getCodeUserOpenId(String code) {
        return redisService.getValue(Key  + code);
    }

    @Override
    public void removeCodeByOpenId(String code, String openId) {
        redisService.remove(Key  + openId);
        redisService.remove(Key  + code);
    }

    @Override
    public String genCodeTest(String openid) {
        // 首先判断是否已经存在验证码
        String codeOld = redisService.getValue(Key + openid);
        if(StringUtils.isNoneBlank(codeOld)){
            return codeOld;
        }

        // 获取锁，防止多次验证码生成
        RLock lock = redisService.getLock(Key);
        try {
            // 上锁并设置超时时间为15秒
            lock.lock(15, TimeUnit.SECONDS);

            // 生成验证码，这里可能会存在验证码重复的问题，因为我们登录只需要验证码，如果验证码重复，重新生成
            String code = RandomStringUtils.randomNumeric(6);

            // 防重校验&重新生成
            for (int i = 0; i < 10 && StringUtils.isNotBlank(redisService.getValue(Key + "_" + code)); i++) {
                if (i < 3) {
                    code = RandomStringUtils.randomNumeric(6);
                    log.warn("验证码重复，生成6位字符串验证码 {} {}", openid, code);
                } else if (i < 5) {
                    code = RandomStringUtils.randomNumeric(7);
                    log.warn("验证码重复，生成7位字符串验证码 {} {}", openid, code);
                } else if (i < 9) {
                    code = RandomStringUtils.randomNumeric(8);
                    log.warn("验证码重复，生成8位字符串验证码 {} {}", openid, code);
                } else {
                    return "";
                }
            }

            // 保存验证码到缓存
            redisService.setValue(Key + openid, code, 5 * 60 * 1000);
            redisService.setValue(Key + code, openid, 5 * 60 * 1000 );
            return code;
        } finally {
            lock.unlock();
        }
    }
}
