package com.luckysj.chatgpt.data.domain.openai.service.rule.factory;

import com.luckysj.chatgpt.data.domain.openai.annotation.LogicStrategy;
import com.luckysj.chatgpt.data.domain.openai.service.rule.ILogicFilter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 校验规则工厂
 * @create 2023/12/07 19:48:27
 */
@Service
public class DefaultLogicFactory {
    // 过滤规则
    private Map<String, ILogicFilter> logicFilterMap = new ConcurrentHashMap<>();

    public DefaultLogicFactory(List<ILogicFilter> logicFilters) {
        logicFilters.forEach(logic -> {
            // 根据类和注解名找到校验规则上的注解
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
            if (null != strategy) {
                // 以注解值为key，校验规则为logic保存到logicFilterMap中
                logicFilterMap.put(strategy.logicMode().getCode(), logic);
            }
        });
    }

    // 获取校验规则集
    public Map<String, ILogicFilter> openLogicFilter() {
        return logicFilterMap;
    }

    /**
     * 规则逻辑枚举
     */
    public enum LogicModel {

        NULL("NULL", "放行不用过滤"),
        ACCESS_LIMIT("ACCESS_LIMIT", "访问次数过滤"),
        SENSITIVE_WORD("SENSITIVE_WORD", "敏感词过滤"),
        USER_QUOTA("USER_QUOTA", "用户额度过滤"),
        MODEL_TYPE("MODEL_TYPE", "模型可用范围过滤"),
        ACCOUNT_STATUS("ACCOUNT_STATUS", "账户状态过滤"),
        ;

        private String code;
        private String info;

        LogicModel(String code, String info) {
            this.code = code;
            this.info = info;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }
}
