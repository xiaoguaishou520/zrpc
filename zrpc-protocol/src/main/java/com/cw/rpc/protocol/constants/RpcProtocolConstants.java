package com.cw.rpc.protocol.constants;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 封装协议头相关的常量
 */
public class RpcProtocolConstants {

    /**
     * 魔数
     */
    public static final short MAGIC = 0x666;

    /**
     * 协议版本号
     */
    public static final byte VERSION = 0x1;

    /**
     * 协议头长度
     */
    public static final int HEADER_TOTAL_LENGTH = 18;
}
