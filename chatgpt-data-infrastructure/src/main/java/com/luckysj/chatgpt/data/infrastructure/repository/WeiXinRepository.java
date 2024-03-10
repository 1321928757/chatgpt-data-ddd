package com.luckysj.chatgpt.data.infrastructure.repository;

import com.luckysj.chatgpt.data.domain.weixin.repository.IWeiXinRepository;
import com.luckysj.chatgpt.data.infrastructure.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class WeiXinRepository implements IWeiXinRepository {

    // 验证码前缀
    private static final String Key = "weixin_code:";

    @Resource
    private RedisService redisService;

    @Override
    public String genCode(String openid) {
        // 首先判断是否已经存在验证码
        String codeOld = redisService.getValue(Key +  openid);
        if(StringUtils.isNoneBlank(codeOld)){
            return codeOld;
        }

        // 获取锁，防止多次验证码生成
        RLock lock = redisService.getLock(Key);
        try {
            // 上锁并设置超时时间为15秒
            lock.lock(15, TimeUnit.SECONDS);

            // 生成验证码，这里可能会存在验证码重复的问题，因为我们登录只需要验证码，如果验证码重复就要重新生成
            String code = RandomStringUtils.randomNumeric(6);
            // 防重校验&重新生成
            for (int i = 0; i < 10 && StringUtils.isNotBlank(redisService.getValue(Key +  code)); i++) {
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
            redisService.setValue(Key  + openid, code);
            redisService.setValue(Key  + code, openid);
            return code;
        } finally {
            lock.unlock();
        }
    }
}
