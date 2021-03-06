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
public class RRException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public RRException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public RRException(RRExceptionEnum exceptionEnum) {
        this(exceptionEnum.getMsg(), exceptionEnum.getCode());
    }

    public RRException(RRExceptionEnum exceptionEnum, String errorMsg) {
        this(exceptionEnum.getMsg() + Constant.SPLIT_CHAR_COLON + errorMsg, exceptionEnum.getCode());
    }

    public RRException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public RRException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public RRException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }


}
