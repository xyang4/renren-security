/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.renren.common.enums.RRExceptionEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class R {

    @JsonProperty("code")
    private int code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private Object data;

    public static R error() {
        return error(RRExceptionEnum.UNKNOWN_ERROR);
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(Object data) {
        this.code = RRExceptionEnum.REQUEST_SUCCESS.getCode();
        this.msg = RRExceptionEnum.REQUEST_SUCCESS.getMsg();
        this.data = data;
    }

    public static R error(String msg) {
        return R.error(RRExceptionEnum.UNKNOWN_ERROR.getCode(), RRExceptionEnum.UNKNOWN_ERROR.getMsg() + ":" + msg);
    }

    public static R error(RRExceptionEnum rrExceptionEnum) {
        return error(rrExceptionEnum.getCode(), rrExceptionEnum.getMsg());
    }

    public static R error(int code, String msg) {
        return new R(code, msg);
    }

    public static R ok() {
        return new R(RRExceptionEnum.REQUEST_SUCCESS);
    }

    public static R ok(Object data) {
        return new R(data);
    }
}
