package com.luckysj.chatgpt.data.domain.weixin.repository;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 微信服务仓储
 * @create 2023/12/17 20:59:07
 */
public interface IWeiXinRepository {
    String genCode(String openid);
}
