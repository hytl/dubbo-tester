package com.hytl.dubbo.tester.core.config;

/**
 * ServiceConfiguration
 *
 * @author hytl
 */
public record ServiceConfiguration(String serviceInterface, String methodName, String[] parameterTypes,
                                   Object[] parameters) {
}
