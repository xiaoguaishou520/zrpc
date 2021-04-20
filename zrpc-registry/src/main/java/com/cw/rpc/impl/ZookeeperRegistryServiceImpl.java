package com.cw.rpc.impl;

import com.cw.rpc.core.RegistryService;
import com.cw.rpc.core.RpcUtils;
import com.cw.rpc.core.ServiceMeta;
import com.cw.rpc.loadbalancer.impl.ZookeeperServiceLoadBalancerImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.List;

/**
 * @Author 小怪兽
 * @Date 2021-03-31
 * 基于zookeeper的实现方案
 */
public class ZookeeperRegistryServiceImpl implements RegistryService {

    //声明节点的基础路径
    private static final String ZK_BASE_PATH = "/zrpc";

    private ServiceDiscovery<ServiceMeta> serviceDiscovery;

    /**
     * 构造方法连接zk客户端，初始化ServiceDiscovery对象
     * @param registryAddr
     * @throws Exception
     */
    public ZookeeperRegistryServiceImpl(String registryAddr)throws Exception {
        //1.创建一个zookeeper客户端，设置重试策略
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                registryAddr,
                new ExponentialBackoffRetry(1000,3));
        client.start();
        //2.初始化服务发现实例
        //创建一个序列化对象
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        serviceDiscovery.start();
    }

    /**
     * 注册服务
     * @param serviceMeta
     * @throws Exception
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        //创建服务实例的描述对象
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcUtils.buildSeviceKey(serviceMeta.getServiceName(),serviceMeta.getVersion()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        //注册服务
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 注销服务
     * @param serviceMeta
     * @throws Exception
     */
    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        //创建服务实例的描述对象
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcUtils.buildSeviceKey(serviceMeta.getServiceName(),serviceMeta.getVersion()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        //注销服务
        serviceDiscovery.unregisterService(serviceInstance);
    }

    /**
     * 发现服务
     * @param serviceKey
     * @param hashcode
     * @return
     * @throws Exception
     */
    @Override
    public ServiceMeta discovery(String serviceKey, int hashcode) throws Exception {
        //根据serviceKey获取服务列表
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceKey);
        //根据负载均衡策略实现逻辑，获取对应的服务节点
        ServiceInstance<ServiceMeta> serviceInstance =
                new ZookeeperServiceLoadBalancerImpl().select(
                        (List<ServiceInstance<ServiceMeta>>) serviceInstances, hashcode);
        //获取对应的服务元信息
        if (serviceInstance != null) {
            return serviceInstance.getPayload();
        }
        return null;
    }
}
