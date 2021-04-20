package com.cw.rpc.configuration;

import com.cw.rpc.core.RegistryService;
import com.cw.rpc.core.RegistryTypeEnum;
import com.cw.rpc.factory.RegistryServiceFactory;
import com.cw.rpc.properties.RpcProperties;
import com.cw.rpc.provider.RpcProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author 小怪兽
 * @Date 2021-04-01
 * provider配置类
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcProviderConfiguration {

    @Resource
    private RpcProperties rpcProperties;

    @Bean
    public RpcProvider initRpcProvider() throws Exception {
        //获取配置的注册中心类型
        RegistryTypeEnum registryTypeEnum = RegistryTypeEnum.valueOf(rpcProperties.getRegistryType());
        //获取注册中心的地址
        String registryAddr = rpcProperties.getRegistryAddr();
        //根据类型及地址，获取对应注册中心的实现类
        RegistryService registryService = RegistryServiceFactory.getInstance(registryAddr, registryTypeEnum);
        return new RpcProvider(registryService,rpcProperties.getServicePort());
    }
}
