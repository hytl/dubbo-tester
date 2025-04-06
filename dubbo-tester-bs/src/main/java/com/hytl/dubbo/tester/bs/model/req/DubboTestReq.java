package com.hytl.dubbo.tester.bs.model.req;

import com.hytl.dubbo.tester.core.config.enums.RegistryType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.wildfly.common.annotation.NotNull;

import java.util.List;

/**
 * DubboTestReq
 *
 * @author hytl
 */
@Data
public class DubboTestReq {
    // registry configuration
    @NotNull
    private RegistryType registryType;
    @NotBlank
    private String registryAddress;

    // service configuration
    @NotBlank
    private String serviceInterface;
    @NotBlank
    private String methodName;
    private List<String> parameterTypes;
    private List<String> parameters;
}
