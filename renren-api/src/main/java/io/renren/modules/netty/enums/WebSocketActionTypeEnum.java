package io.renren.modules.netty.enums;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WebSocket Action Type ,消息队列消费中也使用到
 */
@Getter
public enum WebSocketActionTypeEnum {
    /**
     * 初始化数据
     */
    INIT("init", "初始化数据"),
    /**
     * 在线，保活连接
     */
    ONLINE("online", "在线，保活连接"),
    /**
     * 开始接单
     */
    BEGIN_RECEIPT("begin_receipt", "开始接单"),
    /**
     * 停止接单
     */
    STOP_RECEIPT("stop_receipt", "停止接单"),
    /**
     * 抢单
     */
    RUSH_ORDERS("rush_orders", "抢单"),
    /**
     * 查询用户订单：已抢进行中的订单
     */
    ONGOING_ORDERS("ongoing_orders", "查询进行中订单"),
    /**
     * 查询用户订单：完成状态的订单
     */
    SUCCESS_ORDERS("success_orders", "查询完成状态订单"),

    PRINT_SERVER_TIME("print_server_time", "打印系统时间"),;

    private String command;
    private String describe;
    private static final ImmutableMap<String, WebSocketActionTypeEnum> ID_MAP;

    WebSocketActionTypeEnum(String command, String desctibe) {
        this.command = command;
        this.describe = desctibe;
    }

    static {
        final ImmutableMap.Builder<String, WebSocketActionTypeEnum> builder = new ImmutableMap.Builder<>();
        for (final WebSocketActionTypeEnum item : WebSocketActionTypeEnum.values()) {
            builder.put(item.getCommand(), item);
        }
        ID_MAP = builder.build();
    }

    public static List<String> getCommands() {
        return Arrays.stream(WebSocketActionTypeEnum.values()).map(v -> v.command).collect(Collectors.toList());
    }

    public static WebSocketActionTypeEnum getByCode(final String code) {
        return ID_MAP.get(code);
    }
}
