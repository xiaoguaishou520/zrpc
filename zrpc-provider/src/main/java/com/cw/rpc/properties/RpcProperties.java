package com.cw.rpc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author 小怪兽
 * @Date 2021-04-01
 */
@Data
@ConfigurationProperties(prefix = "zrpc")
public class RpcProperties {

    private String registryType;
    private String registryAddr;
    private int servicePort;
}
