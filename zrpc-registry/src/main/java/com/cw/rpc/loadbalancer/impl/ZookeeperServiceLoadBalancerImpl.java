package com.cw.rpc.loadbalancer.impl;

import com.cw.rpc.core.ServiceMeta;
import com.cw.rpc.loadbalancer.ServiceLoadBalancer;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author 小怪兽
 * @Date 2021-03-31
 * 基于zookeeper实现负载均衡
 */
public class ZookeeperServiceLoadBalancerImpl implements ServiceLoadBalancer<ServiceInstance<ServiceMeta>> {

    /**
     * 虚拟节点的个数
     */
    private static final int VIRTUAL_NODE_SIZE = 2;

    /**
     * 根据客户端hashcode选择服务端节点
     * @param severs 服务列表
     * @param hashcode 客户端的hashcode
     * @return
     */
    @Override
    public ServiceInstance<ServiceMeta> select(List<ServiceInstance<ServiceMeta>> severs, int hashcode) {
        //1.将服务列表通过一致性hash算法构成一个环
        TreeMap<Integer, ServiceInstance<ServiceMeta>> ring = buildConsistenHashRing(severs);
        //2.根据客户端的hashcode获取到对应的服务实例
        return assignServerNode(ring,hashcode);
    }

    /**
     * 根据客户端的hashcode获取到对应的服务实例
     * @param ring 一致性hash环
     * @param hashcode 客户端hashcode
     * @return
     */
    private ServiceInstance<ServiceMeta> assignServerNode(TreeMap<Integer, ServiceInstance<ServiceMeta>> ring, int hashcode) {
        //获取到大于或等于客户端hashcode的第一个节点
        Map.Entry<Integer, ServiceInstance<ServiceMeta>> instanceEntry = ring.ceilingEntry(hashcode);
        if (instanceEntry == null) {
            //如果找不到，获取第一个节点
            instanceEntry = ring.firstEntry();
        }
        return instanceEntry.getValue();
    }

    /**
     * 将服务列表通过一致性hash算法构成一个环
     * @param severs
     * @return
     */
    private TreeMap<Integer, ServiceInstance<ServiceMeta>> buildConsistenHashRing(List<ServiceInstance<ServiceMeta>> severs) {
        TreeMap<Integer,ServiceInstance<ServiceMeta>> ring = new TreeMap<>();

        //每个节点构建对应的虚拟节点，保存到环中
        for (ServiceInstance<ServiceMeta> sever : severs) {
            //构建一个计算服务器Hash值的组合
            String serverKey = buildServerInstanceKey(sever);
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                ring.put((serverKey + i).hashCode(),sever);
            }
        }
        return ring;
    }

    /**
     * 根据服务器的元信息构建一个描述服务的key
     * @param sever
     * @return
     */
    private String buildServerInstanceKey(ServiceInstance<ServiceMeta> sever) {
        ServiceMeta serviceMeta = sever.getPayload();
        StringBuilder stringBuilder = new StringBuilder(serviceMeta.getServiceAddr());
        stringBuilder.append(":");
        stringBuilder.append(serviceMeta.getServicePort());
        return stringBuilder.toString();
    }
}









