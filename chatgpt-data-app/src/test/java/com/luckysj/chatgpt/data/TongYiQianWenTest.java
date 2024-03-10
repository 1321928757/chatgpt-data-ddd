package com.luckysj.chatgpt.data;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 通义千问测试接口
 * @create 2023/12/16 21:12:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TongYiQianWenTest {
    /**
    * @description 对话测试（非流式）
    * @date 2023/12/16 21:13:20
    */
    @Test
    public void testChatRquest(){
        try {
            Constants.apiKey="sk-d2275c7a8b35449db8fb8b1d053c4ba5";//这里填写自己申请的APIKEY
            Generation gen = new Generation();
            MessageManager msgManager = new MessageManager(10);
            Message systemMsg =
                    Message.builder().role(Role.SYSTEM.getValue()).content("You are a helpful assistant.").build();
            Message userMsg = Message.builder().role(Role.USER.getValue()).content("你好，周末去哪里玩？").build();
            msgManager.add(systemMsg);
            msgManager.add(userMsg);
            QwenParam param =
                    QwenParam.builder().model(Generation.Models.QWEN_PLUS).messages(msgManager.get())
                            .resultFormat(QwenParam.ResultFormat.MESSAGE)
                            .topP(0.8)
                            .enableSearch(true)
                            .build();
            GenerationResult result = gen.call(param);
            System.out.println(result);
            msgManager.add(result);
            System.out.println(JsonUtils.toJson(result));
            param.setPrompt("你好，请问你可以帮我完成哪些工作");
            param.setMessages(msgManager.get());
            result = gen.call(param);
            System.out.println(result);
            System.out.println(JsonUtils.toJson(result));
        } catch (NoApiKeyException e) {
            e.printStackTrace();
        } catch (InputRequiredException e) {
            e.printStackTrace();
        }
    }
}
