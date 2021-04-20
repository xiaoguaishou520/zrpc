package com.cw.rpc.serialization.exception;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 自定义异常框架继承运行时异常，用来描述业务逻辑错误
 */
public class RpcSerializationException extends RuntimeException{

    private static final long serialVersionUID = 6666L;

    public RpcSerializationException(String msg) {
        super(msg);
    }

    public RpcSerializationException(Throwable cause) {
        super(cause);
    }
}
