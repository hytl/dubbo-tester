package com.hytl.dubbo.tester.core.config;

import com.hytl.dubbo.tester.core.config.enums.RegistryType;

/**
 * RegistryConfiguration
 *
 * @author hytl
 */
public record RegistryConfiguration(RegistryType registryType, String registryAddress) {
}
