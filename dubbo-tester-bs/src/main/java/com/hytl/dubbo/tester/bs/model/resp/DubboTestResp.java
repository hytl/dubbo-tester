package com.hytl.dubbo.tester.bs.model.resp;

import lombok.Data;

/**
 * DubboTestResp
 *
 * @author hytl
 */
@Data
public class DubboTestResp {
    private String exception;

    private Object result;
}
