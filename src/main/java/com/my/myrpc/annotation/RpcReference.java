package com.my.myrpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC服务消费者注解
 * 标注在需要注入远程服务的字段上
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    
    /**
     * 服务版本号，默认为空
     */
    String version() default "";
    
    /**
     * 服务分组，默认为空
     */
    String group() default "";
    
    /**
     * 超时时间（毫秒），默认3秒
     */
    long timeout() default 3000;
}
