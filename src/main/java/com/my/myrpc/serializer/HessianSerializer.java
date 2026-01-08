package com.my.myrpc.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.my.myrpc.common.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian序列化实现
 * 相比Java原生序列化，体积小50%以上，速度更快
 */
@Slf4j
public class HessianSerializer implements Serializer {
    
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Hessian2Output output = new Hessian2Output(baos);
            output.writeObject(obj);
            output.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Hessian序列化失败", e);
            throw new RpcException("Hessian序列化失败", e);
        }
    }
    
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            Hessian2Input input = new Hessian2Input(bais);\n            return (T) input.readObject(clazz);
        } catch (IOException e) {
            log.error("Hessian反序列化失败", e);
            throw new RpcException("Hessian反序列化失败", e);
        }
    }
}
