package com.luckysj.chatgpt.data.domain.weixin.service;


import com.luckysj.chatgpt.data.domain.weixin.model.entity.UserBehaviorMessageEntity;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 受理用户行为接口
 * @create 2023/12/05 15:46:14
 */
public interface IWeiXinBehaviorService {

    /**
    * @description 受理公众号用户行为
    * @param
    * @return
    * @date 2023/12/05 15:49:34
    */
    String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity);

}
