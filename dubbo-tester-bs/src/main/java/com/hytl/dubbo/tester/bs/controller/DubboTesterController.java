package com.hytl.dubbo.tester.bs.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.ExceptionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hytl.dubbo.tester.bs.enums.ErrorCode;
import com.hytl.dubbo.tester.bs.exception.DubboTesterExcpetion;
import com.hytl.dubbo.tester.bs.model.RestResult;
import com.hytl.dubbo.tester.bs.model.req.DubboTestReq;
import com.hytl.dubbo.tester.bs.model.resp.DubboTestResp;
import com.hytl.dubbo.tester.core.DubboClient;
import com.hytl.dubbo.tester.core.config.DubboClientConfig;
import com.hytl.dubbo.tester.core.config.RegistryConfiguration;
import com.hytl.dubbo.tester.core.config.ServiceConfiguration;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DubboTesterController
 *
 * @author hytl
 */
@Slf4j
@RequestMapping("/v1/dubbo")
@RestController
public class DubboTesterController {

    @CrossOrigin
    @PostMapping("/service/test")
    public RestResult<DubboTestResp> serviceTest(@RequestBody @Valid DubboTestReq req) throws JsonProcessingException, ClassNotFoundException {
        // 1. Create Registry Configuration
        RegistryConfiguration registryConfiguration = new RegistryConfiguration(
                req.getRegistryType()
                , req.getRegistryAddress());

        // 2. Build Service Configuration
        List<String> parameterTypes = req.getParameterTypes();
        List<String> parameters = req.getParameters();
        ServiceConfiguration serviceConfiguration = new ServiceConfiguration(
                req.getServiceInterface()
                , req.getMethodName()
                , parameterTypes.toArray(new String[0])
                , parseParameters(parameterTypes, parameters));

        // 3. Build DubboClientConfig
        DubboClientConfig dubboClientConfig = new DubboClientConfig(registryConfiguration, serviceConfiguration);

        DubboTestResp dubboTestResp = new DubboTestResp();
        try {
            dubboTestResp.setResult(DubboClient.execute(dubboClientConfig));
        } catch (Exception e) {
            log.error("DubboClient.execute failed", e);
            dubboTestResp.setException(ExceptionUtil.getStackTrace(e));
        }
        return RestResult.ok(dubboTestResp);
    }

    private static Object[] parseParameters(List<String> parameterTypes, List<String> parameters) throws ClassNotFoundException {
        int paramSize = CollectionUtils.size(parameterTypes);
        if (paramSize != CollectionUtils.size(parameters)) {
            throw new DubboTesterExcpetion(ErrorCode.BAD_REQUEST);
        }
        Object[] params = new Object[0];
        if (CollectionUtils.isNotEmpty(parameterTypes)) {
            params = new Object[paramSize];
            for (int i = 0; i < parameterTypes.size(); i++) {
                String parameterType = parameterTypes.get(i);
                Object param = parameters.get(i);
                if (!"java.lang.String".equals(parameterType)) {
                    try {
                        param = JSON.parse(parameters.get(i));
                    } catch (JSONException e) {
                        // ignore
                    }
                }
                params[i] = param;
            }
        }

        return params;
    }
}
