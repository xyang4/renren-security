package io.renren.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "renren")
public class RenrenProperties {

    /**
     * token 鉴权是否开启
     */
    private boolean authOpen = false;
    /**
     * ws 服务端口
     */
    @Value("${renren.web-socket.server.port}")
    private int webSocketServerPort;
    /**
     * ws 端点访问路径
     */
    @Value("${renren.web-socket.server.path}")
    private String webSocketServerPath;
    /**
     * ws 连接有效时长（分钟）
     */
    @Value("${renren.web-socket.expire}")
    private long webSocketExpire;
    /**
     * token 有效连接时间（分钟）
     */
    private Long jwtExpire = 30L;
    /**
     * 金额格式化
     */
    private String decimalFormat;

    private int httpObjMaxContentLength = 65536;
    /**
     * redis 操作日志记录
     */
    private boolean openRedisLogger;
}
