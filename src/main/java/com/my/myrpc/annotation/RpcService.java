package com.my.myrpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC服务提供者注解
 * 标注在服务实现类上，自动注册到注册中心
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    
    /**
     * 服务接口类型，默认为实现的第一个接口
     */
    Class<?> interfaceClass() default void.class;
    
    /**
     * 服务版本号，默认为空
     */
    String version() default "";
    
    /**
     * 服务分组，默认为空
     */
    String group() default "";
}
