package com.luckysj.chatgpt.data.types.annotation;

import java.lang.annotation.*;

/**
 * @description redisson 消息主题注解
 * @create 2023/12/17 21:56:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RedisTopic {

    // 主题名称
    String topic() default "";

}
