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
import io.renren.common.utils.R;
import io.renren.modules.common.domain.RedisCacheKeyConstant;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.handle.WebSocketServerHandler;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.service.ITokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NettyServiceImpl implements INettyService {
    @Autowired
    RenrenProperties renrenProperties;
    @Autowired
    ITokenService iTokenService;
    @Autowired
    IRedisService iRedisService;

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
                        pipeline.addLast(new WebSocketServerProtocolHandler(renrenProperties.getWebSocketServerPath()));
                        // 添加自定义 handler
                        pipeline.addLast(new WebSocketServerHandler(renrenProperties.getOnlineUserInitCapacity()));
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
        if (null == redisMessageDomain || null == redisMessageDomain.getTimestamp() || null == redisMessageDomain.getContent()) {
            return R.error(RRExceptionEnum.MUST_PARAMS_DEFECT_ERROR, redisMessageDomain.toString());
        }

        R r;
        WebSocketActionTypeEnum actionTypeEnum = redisMessageDomain.getTopic();
        if (async) { // 异步处理
            iRedisService.sendMessageToQueue(redisMessageDomain);
            //发送待抢单订单到客户端
            //查询待抢单队列
            //查询在线用户列表
            //判断用户状态
            //判断用户金额
            //这个应该是放在创建订单的地方
            // iRedisService.sendMessageToQueue(actionTypeEnum.getCommand(), content);
            //下发账户余额
            //下发已被抢订单
            //下发用户已付款，确认收款情况
            r = R.ok("推送成功!");
        } else {
            // TODO 同步推送处理
            switch (actionTypeEnum) {
                case PRINT_SERVER_TIME:
                    asyncSendMessage(redisMessageDomain.getContent(), "Now:" + LocalTime.now());
                    break;
                default:
                    log.warn("不支持的事件类型[{}/{}].", actionTypeEnum.getCommand(), actionTypeEnum.getDescribe());
                    break;
            }
            r = R.ok();
        }
        return r;
    }

    @Async
    @Override
    public void asyncSendMessage(String mobile, Object content) {
        if (StringUtils.isBlank(mobile) || null == content) {
            return;
        }
        Channel channel = WebSocketServerHandler.ONLINE_USER_CHANNEL_MAP.get(mobile);
        if (null == channel) {
            log.warn("异步发送消息失败", RRExceptionEnum.USER_NOT_ONLINE);
        } else {
            String msg = (content instanceof String) ? (String) content : JSON.toJSONString(content);
            ChannelFuture channelFuture = channel.writeAndFlush(new TextWebSocketFrame(msg));
            // TODO 推送结果处理
            if (channelFuture.isSuccess()) {
                log.info("消息推送成功!!! 用户[{}]", mobile);
            }
        }
    }

    @Autowired
    OrdersService ordersService;

    @Override
    public R handleWebSocketRequest(WebSocketActionTypeEnum webSocketAction, Channel channel, String token, String
            content) {
        R r;
        // 登录凭证校验
        TokenEntity tokenEntity = iTokenService.queryByToken(token);

        if (null != (r = iTokenService.checkToken(tokenEntity))) {
            return r;
        }

        ChannelId channelId = channel.id();
        log.info("Begin handle WebSocketAction [{}] Token[{}] ChannelId[{}] .", webSocketAction.getDescribe(), token, channelId.asLongText());
        switch (webSocketAction) {
            case ACTIVE:// 保活 存储 key: online:mobile val：longTextId
//                iRedisService.set(RedisCacheKeyConstant.ONLINE_PREFIX + channelId.asLongText(), tokenEntity.getMobile(), renrenProperties.getWebSocketExpire() * 60L, TimeUnit.SECONDS);
                iRedisService.set(RedisCacheKeyConstant.ONLINE_PREFIX + tokenEntity.getMobile(), channelId.asLongText(), renrenProperties.getWebSocketExpire() * 60L, TimeUnit.SECONDS);
                if (!WebSocketServerHandler.ONLINE_USER_WITH_MOBILE.contains(tokenEntity.getMobile())) {
                    WebSocketServerHandler.ONLINE_USER_WITH_MOBILE.add(tokenEntity.getMobile());
                    WebSocketServerHandler.ONLINE_USER_CHANNEL_MAP.put(tokenEntity.getMobile(), channel);
                }
                break;
            case BEGIN_RECEIPT:// 开始接单，将用户追加至可接单队列中
                if (!checkWebSocketUserIsActive(tokenEntity.getMobile(), channel)) {
                    return R.error(RRExceptionEnum.USER_NOT_ONLINE, "请先进行 active 操作");
                }

                // add user to redis set:  key=order_list_can_buy
                if (!iRedisService.isSetMember(RedisCacheKeyConstant.USERS_CAN_RUSH_BUY, tokenEntity.getMobile())) {
                    iRedisService.setAdd(RedisCacheKeyConstant.USERS_CAN_RUSH_BUY, tokenEntity.getMobile());
                }
                return R.ok();

            case STOP_RECEIPT:// 停止接单,从集合中移除
                Long no = iRedisService.removeSetMember(RedisCacheKeyConstant.USERS_CAN_RUSH_BUY, tokenEntity.getMobile());
                return R.ok("Ele No:" + no);

            case RUSH_ORDERS://抢单
                // TODO 抢单处理
                //ridis事物开始 ?
                //查询已抢中订单数据集
                //如果已存在该订单，直接返回，订单已被抢
                //如果不存在则保存订单到已抢订单中
                //计算amount实际订单金额，增加小数点，区分不同订单
                //更新订单状态及信息
                //ridis事物提交
                //下发订单被抢消息
                break;
            case PRINT_SERVER_TIME:
                r = R.ok(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
                break;
            default:
                r = R.error(RRExceptionEnum.BAD_REQUEST_PARAMS, "command[ " + (null == webSocketAction ? "null" : webSocketAction.getCommand()) + " ]");
        }
        return r;
    }

    @Override
    public boolean checkWebSocketUserIsActive(String mobile, Channel channel) {
        ChannelId channelId = channel.id();
        boolean r;
        Long expire = iRedisService.getExpire(RedisCacheKeyConstant.ONLINE_PREFIX + mobile, TimeUnit.SECONDS);
        if (null == expire) {
            r = false;
            if (!WebSocketServerHandler.ONLINE_USER_WITH_MOBILE.contains(mobile)) {
                WebSocketServerHandler.ONLINE_USER_WITH_MOBILE.add(mobile);
                WebSocketServerHandler.ONLINE_USER_CHANNEL_MAP.put(mobile, channel);
            }
        } else {
            r = expire > 0;
        }

        return r;
    }

    @Override
    public Object listOnlineUser() {
        Map<String, Object> rMap = new HashMap<>(2);
        List<Object> mobileList = WebSocketServerHandler.ONLINE_USER_CHANNEL_MAP.keySet().stream().collect(Collectors.toList());
        rMap.put("list", mobileList);
        rMap.put("size", WebSocketServerHandler.ONLINE_USER_CHANNEL_MAP.size());
        return rMap;
    }

    public void send() {
        // 群组用户筛选
        Set<Channel> collect = WebSocketServerHandler.ONLINE_USER_GROUP.stream().filter(v -> {
            ChannelId channelId = v.id();
            return true;
        }).collect(Collectors.toSet());

    }
}
