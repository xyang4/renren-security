package io.renren.modules.websocket.service;

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
    void start(int port) throws InterruptedException;
}
