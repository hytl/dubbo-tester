package com.hytl.dubbo.tester.core.config;

/**
 * DubboClientConfig
 *
 * @author hytl
 */
public record DubboClientConfig(RegistryConfiguration registryConfiguration,
                                ServiceConfiguration serviceConfiguration) {
}
