package com.luckysj.chatgpt.data.domain.weixin.service;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 验签接口
 * @create 2023/12/05 15:45:55
 */
public interface IWeiXinValidateService {

    /**
    * @description 签名校验
    * @param
    * @return boolean 验签结果
    * @date 2023/12/05 15:47:18
    */
    boolean checkSign(String signature, String timestamp, String nonce);

}
