package com.cw.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 */
@Data
public class MessageHeader implements Serializable {

    /**
     * 魔数
     */
    private short magic;

    /**
     * 协议版本号
     */
    private byte version;

    /**
     * 序列化算法类型
     */
    private byte serialization;

    /**
     * 消息类型
     */
    private byte msgType;

    /**
     * 消息状态
     */
    private byte status;

    /**
     * 消息id
     */
    private long msgId;

    /**
     * 数据长度
     */
    private int msgLength;
}
