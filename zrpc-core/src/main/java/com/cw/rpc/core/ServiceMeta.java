package com.cw.rpc.core;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 小怪兽
 * @Date 2021-03-30
 * 描述服务的元信息
 * 服务发布到注册中心，用来描述当前服务的状态
 */
@Data
public class ServiceMeta implements Serializable {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String version;

    /**
     * 服务地址
     */
    private String serviceAddr;

    /**
     * 服务端口
     */
    private int servicePort;
}
