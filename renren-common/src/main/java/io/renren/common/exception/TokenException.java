/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.common.exception;

import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.utils.Constant;
import lombok.Data;

/**
 * 自定义异常
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
public class TokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 403;

    public TokenException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public TokenException(RRExceptionEnum exceptionEnum) {
        this(exceptionEnum.getMsg(), exceptionEnum.getCode());
    }

    public TokenException(RRExceptionEnum exceptionEnum, String errorMsg) {
        this(exceptionEnum.getMsg() + Constant.SPLIT_CHAR_COLON + errorMsg, exceptionEnum.getCode());
    }

    public TokenException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public TokenException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public TokenException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }


}
