package com.luckysj.chatgpt.data.domain.openai.annotation;

import com.luckysj.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) //指定注解可以应用的地方，ElementType.TYPE 表示该注解可以应用在类、接口（包括注解类型）或枚举声明
@Retention(RetentionPolicy.RUNTIME) //指定注解的生命周期，即它在什么时候可用，RetentionPolicy.RUNTIME 表示在运行时可用
public @interface LogicStrategy {
    DefaultLogicFactory.LogicModel logicMode();

}
