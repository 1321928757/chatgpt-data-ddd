package com.luckysj.chatgpt.data.domain.weixin.service.validate;

import com.luckysj.chatgpt.data.domain.weixin.service.IWeiXinValidateService;
import com.luckysj.chatgpt.data.types.sdk.weixin.SignatureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 验签接口实现类
 * @create 2023/12/05 15:46:56
 */
@Service
public class WeiXinValidateService implements IWeiXinValidateService {

    @Value("${wx.config.token}")
    private String token;

    @Override
    public boolean checkSign(String signature, String timestamp, String nonce) {
        return SignatureUtil.check(token, signature, timestamp, nonce);
    }

}
