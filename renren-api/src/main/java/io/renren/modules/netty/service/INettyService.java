package io.renren.modules.netty.service;

import io.netty.channel.Channel;
import io.renren.common.utils.R;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;

/**
 * netty 服务相关
 */
public interface INettyService {
    /**
     * 启动服务
     *
     * @param port
     * @throws InterruptedException
     */
    void start(int port);

    /**
     * 给指定的在线用户发送推送消息
     *
     * @param mobile
     * @param message
     * @param toQueue
     * @return
     */
    R sendMessage(WebSocketActionTypeEnum actionTypeEnum, String mobile, Object message, boolean toQueue);

    /**
     * 处理webSocket 请求
     *
     * @param webSocketAction command
     * @param channel         netty channel
     * @param token           用户鉴权token
     * @param content         请求参数
     * @return
     */
    R handleWebSocketRequest(WebSocketActionTypeEnum webSocketAction, Channel channel, String token, String content);
}
