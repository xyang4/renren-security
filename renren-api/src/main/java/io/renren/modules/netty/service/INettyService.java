package io.renren.modules.netty.service;

import io.netty.channel.Channel;
import io.renren.common.utils.R;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.domain.WebSocketResponseDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import org.springframework.scheduling.annotation.Async;

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
     * 给指定的在线用户发送消息
     *
     * @param messageDomain
     * @param async         是否异步 true|false，true :
     * @return
     */
    R sendMessage(RedisMessageDomain messageDomain, boolean async);

    @Async
    void asyncSendMessage(String mobile, Object content);

    /**
     * 处理webSocket 请求a
     *
     * @param webSocketAction command
     * @param channel         netty channel
     * @param token           用户鉴权token
     * @param content         请求参数
     * @return
     */
    WebSocketResponseDomain handleWebSocketRequest(WebSocketActionTypeEnum webSocketAction, Channel channel, String token, String content);

    /**
     * 检验用户是否活跃
     *
     * @param mobile
     * @param channel
     * @return
     */
    boolean checkWebSocketUserIsActive(String mobile, Channel channel);

    /**
     * 在线用户查询
     *
     * @return
     */
    Object listOnlineUser();

    /**
     * 清理异常在线用户
     */
    void clearActiveUser();
}
