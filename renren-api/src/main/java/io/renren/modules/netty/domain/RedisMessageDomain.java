package io.renren.modules.netty.domain;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 */
@Data
@AllArgsConstructor
@ApiModel
public class RedisMessageDomain {
    /**
     * 消息主题
     * @see io.renren.modules.netty.enums.WebSocketActionTypeEnum
     */
    private String topic;
    /**
     * 用户手机号
     */
    private String mobile;
    /**
     * 消息内容
     */
    private Object content;
}
