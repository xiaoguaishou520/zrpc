package com.cw.rpc.core;

import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 自定义异步调用的返回接口
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcFuture<T> {

    /**
     * 异步调用的结果
     */
    private Promise<T> promise;

    /**
     * 异步调用的超时时间
     */
    private long timeout;
}
