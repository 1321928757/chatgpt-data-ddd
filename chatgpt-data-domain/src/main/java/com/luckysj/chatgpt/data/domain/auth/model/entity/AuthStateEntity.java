package com.luckysj.chatgpt.data.domain.auth.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 鉴权结果
 * @create 2023/12/05 11:26:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthStateEntity {

    private String code;
    private String info;
    private String openId;
    private String token;

}
