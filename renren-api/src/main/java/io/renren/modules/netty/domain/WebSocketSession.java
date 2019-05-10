package io.renren.modules.netty.domain;

import io.netty.channel.Channel;
import lombok.Data;

import java.io.Serializable;


@Data
public class WebSocketSession implements Serializable {
    private String id;
    private Channel channel;
    private long lastCommunicateTimeStamp;

    public static WebSocketSession buildSession(Channel channel) {
        WebSocketSession session = new WebSocketSession();
        session.setChannel(channel);
        session.setId(channel.id().asLongText());
        session.setLastCommunicateTimeStamp(System.currentTimeMillis());
        return session;
    }
}
