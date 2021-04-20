package com.cw.rpc.serialization;

import java.io.IOException;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 序列化接口
 */
public interface RpcSerialization {

    /**
     * 序列化，将对象转换为字节数组
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> byte[] serialization(T obj) throws IOException;

    /**
     * 反序列化，将字节数组转换为对象
     * @param data
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialization(byte[] data,Class<T> clazz) throws IOException;
}
