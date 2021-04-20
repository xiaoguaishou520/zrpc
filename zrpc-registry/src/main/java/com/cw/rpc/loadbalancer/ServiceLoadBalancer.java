package com.cw.rpc.loadbalancer;

import java.util.List;

/**
 * 定义负载均衡实现接口
 */
public interface ServiceLoadBalancer<T> {

    /**
     * 基于一致性hash算法，在服务列表中选择一个合适的节点
     * @param severs 服务列表
     * @param hashcode 客户端的hashcode
     * @return
     */
    T select(List<T> severs,int hashcode);
}
