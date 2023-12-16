package com.luckysj.chatgpt.data.trigger.http;

import com.alibaba.fastjson.JSON;
import com.luckysj.chatgpt.data.domain.auth.model.entity.AuthStateEntity;
import com.luckysj.chatgpt.data.domain.auth.model.valobj.AuthTypeVo;
import com.luckysj.chatgpt.data.domain.auth.service.IAuthService;
import com.luckysj.chatgpt.data.types.common.Constants;
import com.luckysj.chatgpt.data.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 登录授权接口
 * @create 2023/12/05 16:18:06
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/auth/")
public class AuthController {
    @Resource
    private IAuthService authService;

    /**
     * @description 验证码登录授权接口
     * @param code 验证码
     * @return Response<String> data为token的Response
     * @date 2023/12/08 16:29:01
     */
    @PostMapping("/login")
    public Response<String> doLogin(@RequestParam String code){
        try {
            AuthStateEntity authStateEntity = authService.doLogin(code);
            // 校验不通过
            if(!authStateEntity.getCode().equals(AuthTypeVo.CODE_SUCCESS.getCode())){
                return Response.<String>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            //校验通过，放行，携带token信息
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(authStateEntity.getToken())
                    .build();
        } catch (Exception e) {
            log.error("鉴权登录校验失败，验证码: {}，错误消息：{}", code, e.getMessage());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }


    // 获取身份信息测试接口（本地测试用，不需要通过微信公众号登录，防止影响原项目的运行）
    @RequestMapping(value = "getAuth", method = RequestMethod.GET)
    public Response<String> getAuth() {
        AuthStateEntity authStateEntity = authService.getAuthTest();
        return Response.<String>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .data(authStateEntity.getToken())
                .build();
    }

    // 获取身份信息测试接口（本地测试用，不需要通过微信公众号登录，防止影响原项目的运行）
    @RequestMapping(value = "getCode", method = RequestMethod.GET)
    public Response<String> getAuth(@RequestParam String openid) {
        String code = authService.getAuthCode(openid);
        return Response.<String>builder()
                .code(Constants.ResponseCode.SUCCESS.getCode())
                .info(Constants.ResponseCode.SUCCESS.getInfo())
                .data(code)
                .build();
    }
}
