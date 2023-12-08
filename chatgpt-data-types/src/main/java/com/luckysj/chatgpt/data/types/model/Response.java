package com.luckysj.chatgpt.data.types.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 通用结果返回类
 * @create 2023/12/05 11:18:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private String code;
    private String info;
    private T data;

}
