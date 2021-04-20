package com.cw.rpc.consumer;

import com.cw.rpc.core.RpcFuture;
import com.cw.rpc.core.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author 小怪兽
 * @Date 2021-04-02
 * 全局变量管理器
 */
public class RpcRequestHolder {

    /**
     * 全局消息id生成器
     */
    public static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    /**
     * 全局的请求结果容器
     */
    public static final Map<Long, RpcFuture<RpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();

}
