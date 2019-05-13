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
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.handle.WebSocketServerHandler;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.service.TokenService;
import lombok.extern.slf4j.Slf4j;
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
        if (toQueue) {
            //发送待抢单订单到客户端
            //查询待抢单队列

            //查询在线用户列表

            //判断用户状态


            //判断用户金额



            //这个应该是放在创建订单的地方
            // iRedisService.sendMessageToQueue(actionTypeEnum.getCommand(), content);


        } else {
            // 先获取Client channel
            Channel channel = getChannelViaLongTextChannelId(redisMessageDomain.getMobile());
            if (null == channel) {
                return R.error(RRExceptionEnum.USER_NOT_ONLINE);
            }
            channel.writeAndFlush(new TextWebSocketFrame(messageContent));
        }


        //下发账户余额

        //下发已被抢订单

        //下发用户已付款，确认收款情况



        // TODO 发送结果处理
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
            case INIT:// 初始化数据 将用户存到redis中，添加超时时间  TODO
                ChannelId channelId = channel.id();
                iRedisService.putHashKeyWithObject(StaticConstant.REDIS_CACHE_KEY_PREFIX_ONLINE, tokenEntity.getMobile(), channelId.asLongText());
                WebSocketServerHandler.USER_CHANNEL_MAP.put(channelId.asLongText(), channel);
                log.info(">>> R Msg:{}", content);
                r = R.ok(channelId.asShortText() + Constant.SPLIT_CHAR_COLON + channelId.asLongText());
                break;

            case ONLINE:// 刷新超时时间，保活连接
                break;

            case BEGIN_RECEIPT://开始接单 TODO 简单处理 将用户存到redis中
                channelId = channel.id();
                iRedisService.putHashKeyWithObject(StaticConstant.REDIS_CACHE_KEY_PREFIX_ONLINE, tokenEntity.getMobile(), channelId.asLongText());
                WebSocketServerHandler.USER_CHANNEL_MAP.put(channelId.asLongText(), channel);
                log.info(">>> R Msg:{}", content);
                r = R.ok(channelId.asShortText() + Constant.SPLIT_CHAR_COLON + channelId.asLongText());
                break;

            case STOP_RECEIPT://停止接单
               break;

            case RUSH_ORDERS://抢单
            //ridis事物开始 ?
            //查询已抢中订单数据集
            //如果已存在该订单，直接返回，订单已被抢
            //如果不存在则保存订单到已抢订单中
            //计算amount实际订单金额，增加小数点，区分不同订单
            //更新订单状态及信息
            //ridis事物提交
            //下发订单被抢消息
                break;

            case ONGOING_ORDERS://查询用户订单：已抢进行中的订单
                break;

            case SUCCESS_ORDERS://查询用户订单：完成状态的订单
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
