package com.cw.rpc.serialization.impl;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.cw.rpc.serialization.RpcSerialization;
import com.cw.rpc.serialization.exception.RpcSerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 基于Hessian实现序列化
 */
public class HessianSerializationImpl implements RpcSerialization {

    /**
     * Hessian实现序列化
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> byte[] serialization(T obj) throws IOException {
        //1.做好边界判断
        if (obj == null) {
            throw new NullPointerException();
        }
        //2.开始序列化操作
        byte[] result;

        //3.创建一个输出对象
        HessianSerializerOutput hessianSerializerOutput;
        //内存流ByteArrayOutputStream 写到内存中
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            hessianSerializerOutput = new HessianSerializerOutput(outputStream);
            //写入对象，并存储到内存流中
            hessianSerializerOutput.writeObject(obj);
            hessianSerializerOutput.flush();
            //从内存流中获取对应的直接数组
            result = outputStream.toByteArray();
        } catch (Exception e) {
            throw new RpcSerializationException(e);
        }
        return result;
    }

    /**
     * Hessian实现反序列化
     * @param data
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> T deserialization(byte[] data, Class<T> clazz) throws IOException {
        //1.边界判断
        if (data == null) {
            throw new NullPointerException();
        }
        //2.做反序列化操作
        T result;
        HessianSerializerInput hessianSerializerInput;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)) {
            hessianSerializerInput = new HessianSerializerInput(byteArrayInputStream);
            result = (T) hessianSerializerInput.readObject(clazz);
        } catch (Exception e) {
            throw new RpcSerializationException(e);
        }
        return result;
    }
}
