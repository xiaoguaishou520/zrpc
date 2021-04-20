package com.cw.rpc.core;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 封装基础工具类
 */
public class RpcUtils {

    /**
     * 通过服务的名称及版本号构建服务的唯一标识
     * @param ServiceName 服务名称
     * @param version 服务版本号
     * @return 服务唯一标志
     */
    public static String buildSeviceKey(String ServiceName,String version) {
        StringBuilder stringBuilder = new StringBuilder(ServiceName);
        stringBuilder.append("#");
        stringBuilder.append(version);
        return stringBuilder.toString();//UserService#1.0.0
    }
}
