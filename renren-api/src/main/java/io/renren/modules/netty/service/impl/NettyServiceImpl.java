package io.renren.modules.netty.service.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.renren.common.config.RenrenProperties;
import io.renren.modules.netty.handle.WebSocketServerHandler;
import io.renren.modules.netty.service.INettyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NettyServiceImpl implements INettyService {
    @Autowired
    RenrenProperties renrenProperties;

    @Override
    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        //  添加 http 编解码器，webSocket基于http
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(renrenProperties.getHttpObjMaxContentLength()));
                        // 添加对大数据流的支持
                        pipeline.addLast(new ChunkedWriteHandler());
                        // 添加 webSocket 服务器处理协议，使用 传输数据frames
                        pipeline.addLast(new WebSocketServerProtocolHandler(renrenProperties.getWebSocketPath()));
                        // 添加自定义 handler
                        pipeline.addLast(new WebSocketServerHandler());
                    }
                });
//
        try {
            serverBootstrap.bind(port).sync().channel();
            log.info("WebSocket Server Started at port {}.", port);
//            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Netty Server 绑定异常:", e);
        } /*finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }*/
    }

}
