package com.my.myrpc.serializer;

import com.my.myrpc.common.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂
 * 支持多种序列化算法，通过类型标识动态切换
 */
@Slf4j
public class SerializerFactory {
    
    /**
     * 序列化类型常量
     */
    public static final byte JDK_SERIALIZER = 0;
    public static final byte HESSIAN_SERIALIZER = 1;
    public static final byte PROTOBUF_SERIALIZER = 2;
    
    private static final Map<Byte, Serializer> serializerMap = new HashMap<>();
    
    static {
        // 注册所有序列化器
        serializerMap.put(JDK_SERIALIZER, new JdkSerializer());
        serializerMap.put(HESSIAN_SERIALIZER, new HessianSerializer());
        // TODO: 注册Protobuf序列化器
    }
    
    /**
     * 根据类型获取序列化器
     * @param serializerType 序列化器类型
     * @return 序列化器实例
     */
    public static Serializer getSerializer(byte serializerType) {
        Serializer serializer = serializerMap.get(serializerType);
        if (serializer == null) {
            log.warn("未知的序列化类型: {}, 使用默认Hessian序列化器", serializerType);
            serializer = serializerMap.get(HESSIAN_SERIALIZER);
        }
        return serializer;
    }
    
    /**
     * 获取默认序列化器（Hessian）
     */
    public static Serializer getDefaultSerializer() {
        return getSerializer(HESSIAN_SERIALIZER);
    }
}
