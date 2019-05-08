package io.renren.modules.netty.handle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * 在线用户
     */
    public static ChannelGroup ONLINE_USER_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端传输过来的消息
        while (true) {
            exeWebSocketFrameHandle(ctx, msg);
            ctx.flush();
            Thread.sleep(2 * 1000);
        }

    }

    /**
     * @param ctx
     * @param msg
     */
    private void exeWebSocketFrameHandle(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        // TODO 发给自己
        Channel receiverChannel = ctx.channel();
        TextWebSocketFrame socketFrame = new TextWebSocketFrame("Receive Your[" + receiverChannel.id().asLongText() + "-" + receiverChannel.id().asLongText() + "] Msg[" + msg.text() + "], Date[" + LocalDateTime.now() + "].");
        receiverChannel.writeAndFlush(socketFrame);
    }

    /**
     * 客户上线处理： 获取客户端的 channel，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        log.info("用户[ Channel - {}] 上线~~", channel.id().asShortText());
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
        log.info("用户[ Channel - {}]下线!", channelId);

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