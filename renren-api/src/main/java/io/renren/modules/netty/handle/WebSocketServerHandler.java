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
import io.renren.common.utils.R;
import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.netty.domain.WebSocketRequestDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.service.INettyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * 在线用户
     */
    public static ChannelGroup ONLINE_USER_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static Map<String, Channel> USER_CHANNEL_MAP = new HashMap<>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) {
        Channel channel = ctx.channel();
        String msgContent = textWebSocketFrame.text();
        TextWebSocketFrame socketFrame;
        if (StringUtils.isBlank(msgContent)) {
            socketFrame = new TextWebSocketFrame("无效的请求参数");
        } else {
            WebSocketRequestDomain requestDomain = JSONObject.parseObject(msgContent, WebSocketRequestDomain.class);
            WebSocketActionTypeEnum webSocketAction = requestDomain.getWebSocketAction();
            INettyService iNettyService = SpringContextUtils.getBean(INettyService.class);
            R handleR = iNettyService.handleWebSocketRequest(webSocketAction, channel, requestDomain.getToken(), requestDomain.getContent());
            socketFrame = new TextWebSocketFrame(JSON.toJSONString(handleR));
        }
        channel.writeAndFlush(socketFrame);
    }

    /**
     * 客户上线处理： 获取客户端的 channel，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        log.info("用户[{}]（ChannelId） 已上线...", channel.id().asShortText());
        ONLINE_USER_GROUP.add(channel);
    }

    /**
     * 客户下线处理：剔除用户
     *
     * @param ctx
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {

        String channelId = ctx.channel().id().asShortText();
        log.info("用户[{}]（ChannelId）已下线!", channelId);

        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        ONLINE_USER_GROUP.remove(ctx.channel());
    }

    /**
     * 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        ctx.channel().close();
        ONLINE_USER_GROUP.remove(ctx.channel());
    }
}