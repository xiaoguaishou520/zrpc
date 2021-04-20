package com.cw.rpc.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 小怪兽
 * @Date 2021-04-01
 * 本地缓存，用于保存服务key和服务实例的映射关系
 */
public class LocalCache {

    public static final Map<String,Object> rpcServiceMap = new ConcurrentHashMap();
}
