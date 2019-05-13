package io.renren.modules.netty.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 */
@Data
@AllArgsConstructor
public class RedisMessageDomain {
    private String topic;
    private String mobile;
    private Object content;
}
