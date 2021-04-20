package com.cw.rpc.factory;

import com.cw.rpc.core.RegistryService;
import com.cw.rpc.core.RegistryTypeEnum;
import com.cw.rpc.impl.EurekaRegistryServiceImpl;
import com.cw.rpc.impl.ZookeeperRegistryServiceImpl;

/**
 * @Author 小怪兽
 * @Date 2021-03-31
 * 注册中心工厂类
 */
public class RegistryServiceFactory {

    private static volatile RegistryService registryService;

    /**
     * 基于注册中心的类型，返回注册中心实现类
     * @param registryAddr
     * @param typeEnum
     * @return
     */
    public static RegistryService getInstance(String registryAddr, RegistryTypeEnum typeEnum) throws Exception {
        if (registryService == null) {
            synchronized (RegistryServiceFactory.class) {
                if (registryService == null) {
                    switch (typeEnum) {
                        case ZOOKEEPER:
                            registryService = new ZookeeperRegistryServiceImpl(registryAddr);
                            break;
                        case EUREKA:
                            registryService = new EurekaRegistryServiceImpl();
                            break;
                        default:
                            throw new IllegalArgumentException("registry type is illegal" + typeEnum);
                    }
                }
            }
        }
        return registryService;
    }
}
