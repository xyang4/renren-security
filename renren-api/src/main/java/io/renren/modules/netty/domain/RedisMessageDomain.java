package io.renren.modules.netty.domain;

import com.alibaba.fastjson.JSONObject;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 */
@Data
@AllArgsConstructor
public class RedisMessageDomain {
    /**
     * 消息主题
     *
     * @see io.renren.modules.netty.enums.WebSocketActionTypeEnum
     */
    private WebSocketActionTypeEnum topic;
    /**
     * 消息时间戳
     */
    private Long timestamp;
    /**
     * 消息内容
     */
    private String content;

    public RedisMessageDomain(WebSocketActionTypeEnum topic, Long timestamp, Object content) {
        this.topic = topic;
        this.timestamp = timestamp;
        this.content = (content instanceof String) ? (String) content : JSONObject.toJSONString(content);
    }

}
