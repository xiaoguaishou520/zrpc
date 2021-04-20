package com.cw.rpc.consumer;

import com.cw.rpc.core.RegistryService;
import com.cw.rpc.core.RegistryTypeEnum;
import com.cw.rpc.factory.RegistryServiceFactory;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @Author 小怪兽
 * @Date 2021-04-02
 */
public class RpcReferenceBean implements FactoryBean<Object> {

    @Setter
    private Class<?> interfaceClass;
    @Setter
    private String registryType;
    @Setter
    private String registryAddr;
    @Setter
    private String version;
    @Setter
    private long timeout;

    private Object object;

    @Override
    public Object getObject() throws Exception {
        //返回实例后具体bean
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        //返回bean的类型
        return interfaceClass;
    }


    /**
     * 完成object对象的实例化
     * 生成一个动态代理对象
     * @throws Exception
     */
    public void init() throws Exception {
        //1.获取注册中心的操作实例对象
        RegistryService registryService = RegistryServiceFactory.getInstance(
                registryAddr, RegistryTypeEnum.valueOf(registryType));
        //2.构建代理对象
        this.object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcInvokerProxy(registryService,version,timeout));

    }
}
