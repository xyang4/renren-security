package io.renren.modules.netty.service.impl;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.renren.common.config.RenrenProperties;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.util.StaticConstant;
import io.renren.common.utils.Constant;
import io.renren.common.utils.R;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.handle.WebSocketServerHandler;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Override
    public R sendMessage(RedisMessageDomain redisMessageDomain, boolean async) {
        if (null == redisMessageDomain || StringUtils.isAnyBlank(redisMessageDomain.getMobile())) {
            return R.error(RRExceptionEnum.MUST_PARAMS_DEFECT_ERROR, redisMessageDomain.toString());
        }

        String messageContent = JSON.toJSONString(redisMessageDomain);

        if (async) { // 异步处理
            iRedisService.sendMessageToQueue(redisMessageDomain.getTopic(), messageContent);
        } else {
            // 先获取Client channel
            Channel channel = getChannelViaLongTextChannelId(redisMessageDomain.getMobile());
            if (null == channel) {
                return R.error(RRExceptionEnum.USER_NOT_ONLINE);
            }
            channel.writeAndFlush(new TextWebSocketFrame(messageContent));
        }
        return R.ok();
    }

    /**
     * 获取 channel
     *
     * @param mobile
     * @return
     */
    public Channel getChannelViaLongTextChannelId(String mobile) {
        String channelIdLongText = iRedisService.hGet(StaticConstant.REDIS_CACHE_KEY_PREFIX_ONLINE, mobile);
        return WebSocketServerHandler.USER_CHANNEL_MAP.get(channelIdLongText);
    }

    @Autowired
    TokenService tokenService;
    @Autowired
    IRedisService iRedisService;

    @Override
    public R handleWebSocketRequest(WebSocketActionTypeEnum webSocketAction, Channel channel, String token, String content) {
        R r;
        // 登录凭证校验
        TokenEntity tokenEntity = tokenService.queryByToken(token);

        if (null != (r = tokenService.checkToken(tokenEntity))) {
            return r;
        }
        ChannelId channelId = channel.id();
        log.info("Begin handle WebSocketAction [{}] Token[{}] ChannelId[{}] .", webSocketAction, token, channelId.asLongText());
        switch (webSocketAction) {
            case BEGIN_RECEIPT:
                // TODO 简单处理 将用户存到redis中
                iRedisService.putHashKeyWithObject(StaticConstant.REDIS_CACHE_KEY_PREFIX_ONLINE, tokenEntity.getMobile(), channelId.asLongText());
                WebSocketServerHandler.USER_CHANNEL_MAP.put(channelId.asLongText(), channel);
                log.info(">>> R Msg:{}", content);
                r = R.ok(channelId.asShortText() + Constant.SPLIT_CHAR_COLON + channelId.asLongText());
                break;
            case PRINT_SERVER_TIME:
                r = R.ok(LocalDateTime.now());
                break;
            default:
                r = R.error(RRExceptionEnum.BAD_REQUEST_PARAMS, "command[ " + (null == webSocketAction ? "null" : webSocketAction.getCommand()) + " ]");
        }
        return r;
    }

}
