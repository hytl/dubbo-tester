package com.hytl.dubbo.tester.bs.model;

import com.hytl.dubbo.tester.bs.enums.ErrorCode;
import lombok.Data;

/**
 * RestResult
 *
 * @author hytl
 */
@Data
public class RestResult<D> {
    private Integer code;
    private String msg;
    private D data;

    private static RestResult REST_RESULT_OK = new RestResult(ErrorCode.OK);

    public RestResult(Integer code, String msg, D data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public RestResult(ErrorCode restCode) {
        this.code = restCode.getCode();
        this.msg = restCode.getMsg();
    }

    public RestResult(ErrorCode restCode, String msg) {
        this.code = restCode.getCode();
        this.msg = msg;
    }

    public static RestResult ok(Object data) {
        return new RestResult<>(ErrorCode.OK.getCode(), ErrorCode.OK.getMsg(), data);
    }

    public static RestResult ok() {
        return REST_RESULT_OK;
    }
}
