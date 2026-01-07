package com.my.myrpc.serializer;

/**
 * 序列化器接口
 */
public interface Serializer {
    
    /**
     * 序列化
     * @param obj 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);
    
    /**
     * 反序列化
     * @param bytes 字节数组
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 反序列化后的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
