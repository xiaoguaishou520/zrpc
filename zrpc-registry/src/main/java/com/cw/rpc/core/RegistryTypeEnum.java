package com.cw.rpc.core;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心类型枚举类
 */
public enum RegistryTypeEnum {

    ZOOKEEPER,
    EUREKA,
    NACOS;
}