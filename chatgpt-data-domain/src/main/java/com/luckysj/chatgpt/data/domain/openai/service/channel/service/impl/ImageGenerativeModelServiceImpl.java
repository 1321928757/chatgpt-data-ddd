package com.luckysj.chatgpt.data.domain.openai.service.channel.service.impl;

import cn.bugstack.chatgpt.common.Constants;
import cn.bugstack.chatgpt.domain.images.ImageEnum;
import cn.bugstack.chatgpt.domain.images.ImageRequest;
import cn.bugstack.chatgpt.domain.images.ImageResponse;
import cn.bugstack.chatgpt.domain.images.Item;
import cn.bugstack.chatgpt.session.OpenAiSession;
import com.luckysj.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.luckysj.chatgpt.data.domain.openai.model.entity.MessageEntity;
import com.luckysj.chatgpt.data.domain.openai.service.channel.service.IGenerativeModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 图片生成服务
 * @create 2023/12/18 17:50:07
 */
@Slf4j
@Service
public class ImageGenerativeModelServiceImpl implements IGenerativeModelService {

    @Autowired(required = false)
    protected OpenAiSession openAiSession;

    @Override
    public void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws IOException {
        if (null == openAiSession) {
            emitter.send("模型调用未开启，可以选择其他模型对话！");
            emitter.complete();
            return;
        }

        emitter.send("图画开始生成，平均时间为10s-20s,请耐心等待~\n");
        emitter.send("-------------------------------------\n");

        // 异步执行绘画请求，不异步执行的话会导致上面的提示信息无法提前响应给前端
        CompletableFuture.runAsync(() -> {
            try {
                // 封装请求信息，这里我们提取最近的的用户消息作为上下文
                StringBuilder prompt = new StringBuilder();
                List<MessageEntity> messages = chatProcess.getMessages();
                for (MessageEntity message : messages) {
                    String role = message.getRole();
                    if (Constants.Role.USER.getCode().equals(role)) {
                        prompt.append(message.getContent());
                        prompt.append("\r\n");
                    }
                }

                // 绘图请求信息
                ImageRequest request = ImageRequest.builder()
                        .prompt(prompt.toString())
                        .model(chatProcess.getModel())
                        .size(ImageEnum.Size.size_1024.getCode())
                        .build();

                // 异步请求绘图
                ImageResponse imageResponse = openAiSession.genImages(request);
                List<Item> items = imageResponse.getData();

                // chatgpt可以一次生成多张图片，默认是一张
                for (Item item : items) {
                    String url = item.getUrl();
                    log.info("url:{}", url);
                    // md5格式输出给前端
                    emitter.send("![](" + url + ")");
                }

                emitter.complete();
            } catch (IOException e) {
                log.error("Error generating images: {}", e.getMessage(), e);
                emitter.completeWithError(e);
            }
        });

    }

}
