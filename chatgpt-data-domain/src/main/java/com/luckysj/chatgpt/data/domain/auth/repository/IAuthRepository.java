package com.luckysj.chatgpt.data.domain.auth.repository;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 用户账户仓储服务
 * @create 2023/12/10 16:48:59
 */
public interface IAuthRepository {
    /**
    * @description 新增账户
    * @param
    * @return
    * @date 2023/12/10 16:50:38
    */
    boolean insertUserIfNotExist(String openid);
}
