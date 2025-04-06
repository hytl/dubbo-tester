package com.hytl.dubbo.tester.bs.exception;

import com.hytl.dubbo.tester.bs.enums.ErrorCode;

/**
 * DubboTesterExcpetion
 *
 * @author hytl
 */
public class DubboTesterExcpetion extends RuntimeException {
    private int code;

    public DubboTesterExcpetion(ErrorCode errorCode) {
        super(errorCode.getMsg());
    }

    public DubboTesterExcpetion(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMsg(), cause);
        this.code = errorCode.getCode();
    }

    public DubboTesterExcpetion(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public DubboTesterExcpetion(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}