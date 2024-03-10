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

    /**
    * @description 获取验证码
    * @date 2023/12/17 21:30:03
    */
    String getCodeUserOpenId(String code);

    /**
     * @description 通过openid删除验证码
     * @date 2023/12/17 21:30:03
     */
    void removeCodeByOpenId(String code, String openId);

    /**
    * @description 本地测试生成验证码
    * @date 2023/12/17 21:38:32
    */
    String genCodeTest(String openid);
}
