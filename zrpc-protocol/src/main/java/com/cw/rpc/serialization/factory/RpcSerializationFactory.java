package com.cw.rpc.serialization.factory;

import com.cw.rpc.protocol.rpc_enum.RpcSerializationTypeEnum;
import com.cw.rpc.serialization.RpcSerialization;
import com.cw.rpc.serialization.impl.HessianSerializationImpl;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * rpc序列化工厂类，根据协议头序列化类型返回具体的序列化实现类
 */
public class RpcSerializationFactory {

    public static RpcSerialization getRpcSerialization(byte type) {
        //根据type获取对应的序列化类型
        RpcSerializationTypeEnum serializationTypeEnum = RpcSerializationTypeEnum.findByType(type);
        //根据枚举类型，返回对应的序列化实现类对象
        switch (serializationTypeEnum) {
            case HESSIAN:
                return new HessianSerializationImpl();
            default:
                throw new IllegalArgumentException("serialization type illegal" + type);
        }
    }
}
