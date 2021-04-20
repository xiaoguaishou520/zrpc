package com.cw.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 协议包完整类
 */
@Data
public class RpcProtocol<T> implements Serializable {

    /**
     * 协议头
     */
    private MessageHeader header;

    /**
     * 协议体
     */
    private T body;
}
