package com.hk.simba.license.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zengry
 * @description
 * @since 2019/12/27
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class CommonException extends RuntimeException {
    private static final long serialVersionUID = -4730958483656900241L;

    private String msg;
    private int code = 500;

    public CommonException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public CommonException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public CommonException(int code, String msg) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public CommonException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }
}
