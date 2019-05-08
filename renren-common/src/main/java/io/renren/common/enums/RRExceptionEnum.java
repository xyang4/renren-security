package io.renren.common.enums;

import lombok.Getter;

@Getter
public enum RRExceptionEnum {
    REQUEST_SUCCESS(200, "请求成功"), UNKNOWN_ERROR(500, "未知异常，请联系管理员"), DB_ERROR_RECORD_EXIST(501, "数据库中已存在该记录");

    private int code;
    private String msg;

    RRExceptionEnum(int code, String message) {
        this.code = code;
        this.msg = message;
    }
}
