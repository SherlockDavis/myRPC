package com.my.myrpc.serializer;

import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Protobuf序列化器
 * 注意：要求待序列化的对象必须是Protobuf生成的Message类型
 */
@Slf4j
public class ProtobufSerializer implements Serializer {
    
    @Override
    public byte[] serialize(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("序列化对象不能为null");
        }
        
        if (!(obj instanceof Message)) {
            throw new IllegalArgumentException("Protobuf序列化要求对象必须是com.google.protobuf.Message类型");
        }
        
        try {
            Message message = (Message) obj;
            return message.toByteArray();
        } catch (Exception e) {
            log.error("Protobuf序列化失败", e);
            throw new RuntimeException("Protobuf序列化失败", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("反序列化数据不能为空");
        }
        
        if (!Message.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Protobuf反序列化要求目标类必须是com.google.protobuf.Message类型");
        }
        
        try {
            // 通过反射调用parseFrom方法
            Method parseFromMethod = clazz.getMethod("parseFrom", byte[].class);
            return (T) parseFromMethod.invoke(null, bytes);
        } catch (Exception e) {
            log.error("Protobuf反序列化失败", e);
            throw new RuntimeException("Protobuf反序列化失败", e);
        }
    }
}
