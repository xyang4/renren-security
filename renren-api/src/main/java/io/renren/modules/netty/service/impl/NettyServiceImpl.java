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
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(renrenProperties.getHttpObjMaxContentLength()));
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler(renrenProperties.getWebSocketPath()));
                        pipeline.addLast(new WebSocketServerHandler());
                    }
                });
//
        try {
            serverBootstrap.bind(port).sync().channel();
            log.info("Web socket Server Started at port {}.", port);
//            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Netty Server Error:", e);
        } /*finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }*/
    }

}
