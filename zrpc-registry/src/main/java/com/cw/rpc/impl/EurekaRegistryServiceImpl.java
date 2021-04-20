package com.cw.rpc.impl;

import com.cw.rpc.core.RegistryService;
import com.cw.rpc.core.ServiceMeta;

/**
 * @Author 小怪兽
 * @Date 2021-03-31
 */
public class EurekaRegistryServiceImpl implements RegistryService {
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public ServiceMeta discovery(String serviceKey, int hashcode) throws Exception {
        return null;
    }
}
