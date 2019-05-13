package io.renren.common.enums;

import lombok.Getter;

@Getter
public enum RRExceptionEnum {
    REQUEST_SUCCESS(200, "请求成功"),
    // ============= server error
    UNKNOWN_ERROR(500, "未知异常，请联系管理员"), DB_ERROR_RECORD_EXIST(501, "数据库中已存在该记录"),
    // ============= client error
    BAD_REQUEST_PARAMS(400, "请求参数错误"), MUST_PARAMS_DEFECT_ERROR(400, "必传参数缺失"),
    LOGIN_TOKEN_EXPIRE(403, "登录凭证失效，请重新进行登录授权"), USER_NOT_ONLINE(401, "用户未在线");
    private int code;
    private String msg;

    RRExceptionEnum(int code, String message) {
        this.code = code;
        this.msg = message;
    }
}
