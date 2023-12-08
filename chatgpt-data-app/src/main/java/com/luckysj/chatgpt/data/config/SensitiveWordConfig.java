package com.luckysj.chatgpt.data.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.utils.InnerWordCharUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 敏感词配置
 * @create 2023/12/07 11:34:08
 */
@Slf4j
@Configuration
public class SensitiveWordConfig {

    @Bean
    public SensitiveWordBs sensitiveWordBs(){
        return SensitiveWordBs.newInstance()
                // 开启下述后可以记录日志，但是发现替换词变成了空格而不是*，暂不知如何解决
                // .wordReplace((stringBuilder, chars, wordResult, iWordContext) -> {
                //     String sensitiveWord = InnerWordCharUtils.getString(chars, wordResult);
                //     log.info("检测到敏感词: {}", sensitiveWord);
                // })
                .ignoreCase(true) //忽略大小写
                .ignoreWidth(true) //忽略半角圆角
                .ignoreNumStyle(true) //忽略数字的写法
                .ignoreChineseStyle(true) //忽略中文的书写格式
                .ignoreEnglishStyle(true) //忽略英文的书写格式
                .ignoreRepeat(false) //忽略重复词
                .enableNumCheck(true) //是否启用数字检测。
                .enableEmailCheck(true) //是有启用邮箱检测
                .enableUrlCheck(true) //是否启用链接检测
                .enableWordCheck(true) //是否启用敏感单词检测
                .numCheckLen(1024) //数字检测，自定义指定长度
                .init();
    }
}
