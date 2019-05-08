package io.renren.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "renren")
@Data
public class RenrenProperties {
    private boolean redisOpen = false;
    private boolean authOpen = false;

    private int nettyPort = 10001;
    private String webSocketUrl;
    /**
     * 默认30分钟
     */
    private Long jwtExpire = 30L;

    private String decimalFormat;
}
