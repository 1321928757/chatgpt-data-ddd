package com.luckysj.chatgpt.data.domain.openai.model.entity;

import com.luckysj.chatgpt.data.domain.openai.model.valobj.UserAccountStatusVO;
import com.luckysj.chatgpt.data.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 用户账户额度实体对象
 * @create 2023/12/09 17:09:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountQuotaEntity {

    /**
     * 用户ID
     */
    private String openid;
    /**
     * 总量额度
     */
    private Integer totalQuota;
    /**
     * 剩余额度
     */
    private Integer surplusQuota;
    /**
     * 账户状态
     */
    private UserAccountStatusVO userAccountStatusVO;
    /**
     * 模型类型；一个卡支持多个模型调用，这代表了允许使用的模型范围
     */
    private List<String> allowModelTypeList;

    public void transModelTypes(String modelTypes) {
        String[] vals = modelTypes.split(Constants.SPLIT);
        this.allowModelTypeList = Arrays.asList(vals);
    }

}
