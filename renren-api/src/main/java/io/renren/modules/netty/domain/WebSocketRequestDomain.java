package io.renren.modules.netty.domain;

import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import lombok.Data;

@Data
public class WebSocketRequestDomain {
    /**
     * 请求 command
     */
    private String command;
    /**
     * 鉴权使用的唯一Token
     */
    private String token;

    private String content;

    public WebSocketActionTypeEnum getWebSocketAction() {
        return WebSocketActionTypeEnum.getByCode(command);
    }
}
