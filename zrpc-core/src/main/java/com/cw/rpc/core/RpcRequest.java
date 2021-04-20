package com.cw.rpc.core;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 封装客户端的请求信息
 */
@Data
public class RpcRequest implements Serializable {

    /**
     * 调用的类名
     */
    private String className;

    /**
     * 调用的方法名
     */
    private String methodName;

    /**
     * 调用的参数数据类型
     */
    private Class<?>[] paramterTypes;

    /**
     * 调用的参数值
     */
    private Object[] params;

    /**
     * 调用的版本号
     */
    private String version;
}
