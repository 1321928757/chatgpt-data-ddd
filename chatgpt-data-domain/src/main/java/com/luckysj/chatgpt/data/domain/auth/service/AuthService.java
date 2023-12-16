package com.luckysj.chatgpt.data.domain.auth.service;

import com.google.common.cache.Cache;
import com.luckysj.chatgpt.data.domain.auth.model.entity.AuthStateEntity;
import com.luckysj.chatgpt.data.domain.auth.model.valobj.AuthTypeVo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 鉴权服务
 * @create 2023/12/05 15:25:06
 */
@Slf4j
@Service
public class AuthService extends AbstractAuthService{
    @Resource
    private Cache<String, String> cacheCode;


    @Override
    public AuthStateEntity checkCode(String code){
        // TODO 这个设置个魔法值，为了给支付审核的人通过
        if(code.equals("1145")){
            return AuthStateEntity.builder()
                    .code(AuthTypeVo.CODE_SUCCESS.getCode())
                    .info(AuthTypeVo.CODE_SUCCESS.getInfo())
                    .openId("xfg")
                    .build();
        }

        // 从缓存中读取验证码
        String openId = cacheCode.getIfPresent(code);
        if(StringUtils.isBlank(openId)){
            log.info("鉴权失败，用户输入的验证码不存在 {}", code);
            return AuthStateEntity.builder()
                    .code(AuthTypeVo.CODE_NOT_EXIST.getCode())
                    .info(AuthTypeVo.CODE_NOT_EXIST.getInfo())
                    .build();
        }

        // 移除缓存Key值
        cacheCode.invalidate(openId);
        cacheCode.invalidate(code);

        // 返回校验成功结果
        return AuthStateEntity.builder()
                .code(AuthTypeVo.CODE_SUCCESS.getCode())
                .info(AuthTypeVo.CODE_SUCCESS.getInfo())
                .openId(openId)
                .build();
    }

    @Override
    public boolean checkToken(String token) {

        return isVerify(token);
    }

    @Override
    public String parseOpenid(String token) {
        Claims claims = decode(token);
        return claims.get("openId").toString();
    }
}
