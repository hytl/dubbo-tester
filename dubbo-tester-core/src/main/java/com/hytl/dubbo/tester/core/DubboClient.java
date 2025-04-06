package com.hytl.dubbo.tester.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hytl.dubbo.tester.core.config.DubboClientConfig;
import com.hytl.dubbo.tester.core.config.RegistryConfiguration;
import com.hytl.dubbo.tester.core.config.ServiceConfiguration;
import com.hytl.dubbo.tester.core.config.enums.RegistryType;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.rpc.service.GenericService;

import java.io.IOException;

/**
 * DubboClient
 *
 * @author hytl
 */
public class DubboClient {
    public static Object execute(DubboClientConfig clientConfig) throws IOException {
        RegistryConfiguration registryConfiguration = clientConfig.registryConfiguration();
        ServiceConfiguration serviceConfiguration = clientConfig.serviceConfiguration();

        // Registry Config
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol(registryConfiguration.registryType().name().toLowerCase());
        registry.setAddress(registryConfiguration.registryAddress().trim());
        registry.setSubscribe(true);
        registry.setDynamic(true);

        // Application Config
        ApplicationConfig applicationConfig = new ApplicationConfig("dubbo-tester-client");
        applicationConfig.setQosEnable(false);
        applicationConfig.setLogger("slf4j");
        applicationConfig.setEnableFileCache(false);
        applicationConfig.setRegisterMode("instance");

        // Reference Config
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface(serviceConfiguration.serviceInterface().trim());
        reference.setGeneric(true);
        reference.setTimeout(20000);
        reference.setCheck(false);
        reference.setScope("remote");
        reference.setCluster("failover");

        DubboBootstrap dubboBootstrap = DubboBootstrap.newInstance()
                .application(applicationConfig)
                .registry(registry)
                .reference(reference)
                .start();

        Object result;
        try {

            // Invoke Service
            GenericService service = reference.get();
            result = service.$invoke(serviceConfiguration.methodName().trim()
                    , serviceConfiguration.parameterTypes(), serviceConfiguration.parameters());
        } finally {
            reference.destroy();
            dubboBootstrap.destroy();
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        RegistryConfiguration registryConfiguration = new RegistryConfiguration(RegistryType.NACOS, "nacos://127.0.0.1:8848");
        ServiceConfiguration serviceConfiguration = new ServiceConfiguration("org.apache.dubbo.samples.quickstart.dubbo.api.DemoService"
                , "sayHello"
                , "java.lang.String".split(",")
                , new ObjectMapper().readValue("[\"world\"]", Object[].class));
        DubboClientConfig dubboClientConfig = new DubboClientConfig(registryConfiguration, serviceConfiguration);
        Object result = DubboClient.execute(dubboClientConfig);
        System.out.println("=================================");
        System.out.println(result);
        System.out.println("=================================");
    }
}
