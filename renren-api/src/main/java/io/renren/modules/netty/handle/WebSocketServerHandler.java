package io.renren.modules.netty.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.common.domain.RedisCacheKeyConstant;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.WebSocketRequestDomain;
import io.renren.modules.netty.domain.WebSocketResponseDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.service.INettyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static int onlineUserInitCapacity;

    public WebSocketServerHandler(int onlineUserInitCapacity) {
        this.onlineUserInitCapacity = onlineUserInitCapacity;
    }

    /**
     * 在线用户
     */
    public static final ChannelGroup ONLINE_USER_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * mobile:channel
     */
    public static Map<String, Channel> ONLINE_USER_CHANNEL_MAP = new HashMap<>(onlineUserInitCapacity);
    /**
     * 存储在线 mobile
     */
    public static List<String> ONLINE_USER_WITH_MOBILE = new ArrayList<>(onlineUserInitCapacity);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) {
        Channel channel = ctx.channel();
        String msgContent = textWebSocketFrame.text();
        TextWebSocketFrame socketFrame;
        WebSocketRequestDomain requestDomain;
        if (StringUtils.isBlank(msgContent) || null == (requestDomain = JSONObject.parseObject(msgContent, WebSocketRequestDomain.class))) {
            socketFrame = new TextWebSocketFrame("无效的请求参数");
        } else {
            WebSocketActionTypeEnum webSocketAction = requestDomain.getWebSocketAction();
            INettyService iNettyService = SpringContextUtils.getBean(INettyService.class);
            WebSocketResponseDomain responseDomain = iNettyService.handleWebSocketRequest(webSocketAction, channel, requestDomain.getToken(), requestDomain.getContent());
            socketFrame = new TextWebSocketFrame(JSON.toJSONString(responseDomain));
        }
        channel.writeAndFlush(socketFrame);
    }

    /**
     * 客户上线处理： 获取客户端的 channel，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String longText = channel.id().asLongText();
        log.info("Channel[{}]已上线!", longText);
        SpringContextUtils.getBean(IRedisService.class).putHashKey(RedisCacheKeyConstant.ONLINE_CHANNEL, longText, "");
        ONLINE_USER_GROUP.add(channel);
    }

    /**
     * 客户下线处理：剔除用户
     *
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        log.info("Channel[{}]已下线!", channel.id().asLongText());
        SpringContextUtils.getBean(INettyService.class).optimizeChannel(channel);
    }

    /**
     * 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //TODO  清除在线的该用户?
        log.error("Web Socket 异常...", cause);
        ctx.channel().close();
    }
}