package com.cw.rpc.protocol.rpc_enum;

import lombok.Getter;

/**
 * 序列化类型枚举类
 */
public enum RpcSerializationTypeEnum {

    HESSIAN(1),
    PROTOBUF(2);

    @Getter
    private int type;

    RpcSerializationTypeEnum(int type) {
        this.type = type;
    }

    /**
     * 根据协议头字段的数值，定位到序列化算法类型
     * @param type
     * @return
     */
    public static RpcSerializationTypeEnum findByType(int type) {
        for (RpcSerializationTypeEnum typeEnum : RpcSerializationTypeEnum.values()) {
            if (typeEnum.getType() == type) {
                return typeEnum;
            }
        }
        return HESSIAN;
    }
}
