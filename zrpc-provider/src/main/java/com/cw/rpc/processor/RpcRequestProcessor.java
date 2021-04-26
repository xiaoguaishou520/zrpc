package com.cw.rpc.processor;

import com.cw.rpc.core.RpcRequest;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author 小怪兽
 * @Date 2021-04-01
 * 接收请求业务线程池
 */
public class RpcRequestProcessor {

    private static volatile ThreadPoolExecutor threadPool;

    public static void submit(Runnable task) {
        //采用双重检测机制完成线程池的初始化
        if (threadPool == null) {
            synchronized (RpcRequestProcessor.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolExecutor(
                            8,8,30,
                            TimeUnit.SECONDS,new LinkedBlockingDeque<>(1000));
                }
            }
        }
        threadPool.submit(task);
    }
}
