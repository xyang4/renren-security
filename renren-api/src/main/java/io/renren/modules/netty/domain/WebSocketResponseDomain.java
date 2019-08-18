package io.renren.modules.netty.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @作者 webSocket 响应实体
 * @描述
 * @编码 UTF-8
 * @时间 2019/6/2 10:57 AM
 */
@Data
@NoArgsConstructor
public class WebSocketResponseDomain {
    private String a;
    private int code = 200;
    private String msg = "请求成功";
    private Object data;

    public WebSocketResponseDomain(String a) {
        this.a = a;
    }
    public WebSocketResponseDomain(String a, Object data) {
        this.a = a;
        this.data = data;
    }

    @Getter
    public enum ResponseCode {
        SUCCESS(200, "请求成功"), REQUEST_ACTION_ERROR(400, "参数错误"),
        ERROR_INVALID_ORDER(400,"无效的订单"),ORDER_AMOUNT_SAME(401, "存在相同金额订单"),
        /**
         * 未在线
         */
        NOT_ACTIVE(403, "用户未在线"), ERROR_HANDLE(500, "操作异常，请联系系统管理员"),
        /**
         *
         */
        ERROR_RUSH_BEING_QUEUE(501, "抢单排队中"),ERROR_RUSH_BY_HASBEAN(502, "订单已被抢"),
        ERROR_RUSH_BY_HASBEAN_ERROR(503, "订单已被抢"),ERROR_RUSH_BY_HASBEAN_USE(504, "订单已被抢");
        private int code;
        private String msg;

        ResponseCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }
}
