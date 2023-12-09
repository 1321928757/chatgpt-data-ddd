package com.luckysj.chatgpt.data.domain.auth.service;

import com.luckysj.chatgpt.data.domain.auth.model.entity.AuthStateEntity;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 登录授权服务接口
 * @create 2023/12/05 14:55:39
 */
public interface IAuthService {
    /**
    * @description 用户登录校验
    * @param code 验证码
    * @return AuthStateEntity 校验状态
    * @date 2023/12/05 14:59:15
    */
    AuthStateEntity doLogin(String code);

    /**
    * @description token校验
    * @param token
    * @return Boolean
    * @date 2023/12/05 15:00:10
    */
    boolean checkToken(String token);

    /**
    * @description  解析token获取openid
    * @param token
    * @return String openid
    * @date 2023/12/09 19:37:18
    */
    String parseOpenid(String token);

    /**
     * @description 本地获取身份测试（开发环境）
     * @return
     * @date 2023/12/05 15:00:10
     */
    AuthStateEntity getAuthTest();
}
