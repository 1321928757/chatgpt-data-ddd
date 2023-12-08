package com.luckysj.chatgpt.data.trigger.http;

import com.alibaba.fastjson.JSON;
import com.luckysj.chatgpt.data.domain.auth.service.IAuthService;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.MessageEntity;
import com.luckysj.chatgpt.data.domain.openai.service.IChatService;
import com.luckysj.chatgpt.data.trigger.http.dto.ChatGPTRequestDTO;
import com.luckysj.chatgpt.data.types.common.Constants;
import com.luckysj.chatgpt.data.types.exception.ChatGPTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author www.luckysj.top 刘仕杰
 * @description chatgpt对话服务接口
 * @create 2023/12/04 19:28:04
 */
@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/")
public class ChatGPTAIServiceController {

    @Resource
    private IChatService chatService;

    @Resource
    private IAuthService authService;

    // 接口测试 curl -X POST http://localhost:7070/api/v1/chatgpt/chat/completions -H "Content-Type: application/json" -H "Authorization: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJvZ2RiNDZEYVl4TjZubGlxamV2ZXFuWk1mcW1JIiwib3BlbklkIjoib2dkYjQ2RGFZeE42bmxpcWpldmVxblpNZnFtSSIsImV4cCI6MTcwMjM3MTI1OCwiaWF0IjoxNzAxNzY2NDU4LCJqdGkiOiIwNTY0YjlhNC0xZjA4LTRkOGItYmJkOC03MmVhOTU2MTIwNjcifQ.mM-qXHMAY6_xdpEGF9I_dOwiwfoKUxyFH_jINSaTeB4" -d "{\"messages\":[{\"content\":\"写一个java冒泡排序\",\"role\":\"user\"}],\"model\":\"gpt-3.5-turbo\"}"
    @RequestMapping(value = "chatgpt/chat/completions", method = RequestMethod.POST)
    public ResponseBodyEmitter completionsStream(@RequestBody ChatGPTRequestDTO request, @RequestHeader("Authorization") String token, HttpServletResponse response) {
        log.info("流式问答请求开始，使用模型：{} 请求信息：{}", request.getModel(), JSON.toJSONString(request.getMessages()));
        try {
            // 1. 基础配置；流式输出、编码、禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            // 2.构造异步响应对象，token验证
            ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
            boolean authResult = authService.checkToken(token);
            // token验证不通过，返回错误码，关闭异步响应
            if (!authResult) {
                try {
                    emitter.send(Constants.ResponseCode.TOKEN_ERROR.getCode());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                emitter.complete();
                return emitter;
            }

            // 2. 构建参数
            ChatProcessAggregate chatProcessAggregate = ChatProcessAggregate.builder()
                    .openid(token)
                    .model(request.getModel())
                    .messages(request.getMessages().stream()
                            .map(entity -> MessageEntity.builder()
                                    .role(entity.getRole())
                                    .content(entity.getContent())
                                    .name(entity.getName())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            // 3. 请求结果&返回
            return chatService.completions(emitter, chatProcessAggregate);
        } catch (Exception e) {
            log.error("流式应答，请求模型：{} 发生异常", request.getModel(), e);
            throw new ChatGPTException(e.getMessage());
        }
    }

}
