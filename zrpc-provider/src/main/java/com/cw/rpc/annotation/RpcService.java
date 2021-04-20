package com.cw.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自义定RPC注解类
 * @Retention(RetentionPolicy.RUNTIME) 表示注解在运行期生效
 * @Target(ElementType.FIELD) 表示该注解加在类描述下
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface RpcService {

    /**
     * 表示描述的服务类型
     * @return
     */
    Class<?> serviceInterface() default Object.class;

    /**
     * 表示服务的版本号
     * @return
     */
    String version() default "1.0.0";
}
