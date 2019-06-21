package io.renren.common.config;

import io.renren.common.util.StaticConstant;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = StaticConstant.PROJECT_PREFIX)
public class RenrenProperties {

    /**
     * token 鉴权是否开启
     */
    private boolean authOpen = false;
    private boolean signOpen = false;
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
    private Long jwtExpire = 60L;
    /**
     * 金额格式化
     */
    private String decimalFormat;

    private int httpObjMaxContentLength = 65536;
    /**
     * redis 操作日志记录
     */
    private boolean openRedisLogger;

    private int onlineUserInitCapacity;

    private String dataSignKey;

    private boolean smsSendOpen = false;
    private String smsCodeDefault;
    private String smsAccount;
    private String smsPassword;
    private String smsUrl;
    private String smsTemplate;

    private long orderRushLockSecond = 3;
    private long batchPushOrderNumMax = 10;
}
