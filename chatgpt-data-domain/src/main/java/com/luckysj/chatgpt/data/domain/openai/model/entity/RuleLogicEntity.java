package com.luckysj.chatgpt.data.domain.openai.model.entity;

import com.luckysj.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 规则校验结果类
 * @create 2023/12/07 19:49:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleLogicEntity <T> {

    private LogicCheckTypeVO type;
    private String info;
    private T data;

}
