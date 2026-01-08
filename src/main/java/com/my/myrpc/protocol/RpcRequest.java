package com.my.myrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC请求消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 请求ID，用于匹配请求和响应
     */
    private String requestId;
    
    /**
     * 接口名称
     */
    private String interfaceName;
    
    /**
     * 方法名称
     */
    private String methodName;
    
    /**
     * 参数类型数组
     * 注意: 类类型是不可序列化的, 序列化时存储类名, 反序列化时需要他簧培曙不体可接变旚
     */
    private Class<?>[] parameterTypes;
    
    /**
     * 参数值数组
     */
    private Object[] parameters;
    
    /**
     * 服务版本号
     */
    private String version;
    
    /**
     * 服务分组
     */
    private String group;
}
