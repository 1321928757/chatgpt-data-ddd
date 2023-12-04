package com.luckysj.chatgpt.data.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 消息实体
 * @create 2023/12/04 19:48:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    private String role;
    private String content;
    private String name;

}
