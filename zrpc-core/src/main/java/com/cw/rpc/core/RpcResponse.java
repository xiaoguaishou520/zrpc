package com.cw.rpc.core;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 封装服务端的响应信息
 */
@Data
public class RpcResponse implements Serializable {

    //响应结果：成功/失败

    /**
     * 响应成功：返回数据
     */
    private Object data;

    /**
     * 响应失败：返回信息
     */
    private String message;
}
