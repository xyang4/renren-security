package io.renren.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "renren")
public class RenrenProperties {
    private boolean redisOpen = false;
    private boolean authOpen = false;

    private int nettyPort = 10001;
    private String webSocketPath;
    /**
     * 默认30分钟
     */
    private Long jwtExpire = 30L;

    private String decimalFormat;

    private int httpObjMaxContentLength = 65536;
    /**
     * redis 操作日志记录
     */
    private boolean openRedisLogger;
}
