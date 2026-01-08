package com.my.myrpc.serializer;

import com.my.myrpc.common.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Java原生序列化实现
 */
@Slf4j
public class JdkSerializer implements Serializer {
    
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("序列化失败", e);
            throw new RpcException("序列化失败", e);
        }
    }
    
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("反序列化失败", e);
            throw new RpcException("反序列化失败", e);
        }
    }
}
