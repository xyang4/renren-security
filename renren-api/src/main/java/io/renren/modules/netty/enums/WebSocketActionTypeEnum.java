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
     * 在线，保活连接
     */
    ACTIVE("active", "存活/在线"),
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

    PUSH_ORDER_TO_SPECIAL_USER("canRushOrdersQueue", "下发订单至指定用户队列"),
    PULL_ORDER("pull_order", "拉取可抢订单"),
    DISTRIBUTE_ORDER("distribute_order", "派发订单"),

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
