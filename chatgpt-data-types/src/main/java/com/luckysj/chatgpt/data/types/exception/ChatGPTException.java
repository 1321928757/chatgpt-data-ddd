package com.luckysj.chatgpt.data.types.exception;
/**
 * @author www.luckysj.top 刘仕杰
 * @description chatgpt会话异常
 * @create 2023/12/04 19:31:16
 */
public class ChatGPTException extends RuntimeException{
    // 异常编码
    private String code;
    // 异常信息
    private String message;

    public ChatGPTException(String code) {
        this.code = code;
    }

    public ChatGPTException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ChatGPTException(String code, String message, Throwable cause) {
        super(message);
        super.initCause(cause);
        this.code = code;
        this.message = message;
    }
}
