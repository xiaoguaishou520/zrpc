package com.cw.rpc.core;

/**
 * @Author 小怪兽
 * @Date 2021-03-31
 * 定义操作注册中心的服务接口
 */
public interface RegistryService {

    /**
     * 实现服务注册
     * @param serviceMeta
     * @throws Exception
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 实现服务的注销
     * @param serviceMeta
     * @throws Exception
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 根据服务名称，实现服务的发现
     * @param serviceKey
     * @param hashcode
     * @return
     * @throws Exception
     */
    ServiceMeta discovery(String serviceKey,int hashcode) throws Exception;


}
