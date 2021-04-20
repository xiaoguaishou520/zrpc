package com.cw.rpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消费者属性注入 注解类
 * @Retention(RetentionPolicy.RUNTIME) 运行期触发
 * @Target(ElementType.FIELD) 注解写在字段属性上
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {

    String registryType() default "ZOOKEEPER";

    String registryAddr() default "127.0.0.1:2181";

    String version() default "1.0.0";

    long timeout() default 3000;
}
