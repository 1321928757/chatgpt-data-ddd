package com.luckysj.chatgpt.data.infrastructure.dao;

import com.luckysj.chatgpt.data.infrastructure.po.UserAccountPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 用户账户dao
 * @create 2023/12/09 17:02:22
 */
@Mapper
public interface IUserAccountDao {
    /**
    * @description 根据openid减少额度
    * @param openid 账户openid
    * @return
    * @date 2023/12/09 17:06:21
    */
    int subAccountQuota(String openid);

    /**
    * @description 根据openid查询用户账户信息
    * @param openid 账户openid
    * @return UserAccountPO 账户实体
    * @date 2023/12/09 17:07:14
    */
    UserAccountPO queryUserAccount(String openid);
}
