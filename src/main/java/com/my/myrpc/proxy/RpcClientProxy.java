package com.my.myrpc.proxy;

import com.my.myrpc.common.constants.RpcConstants;
import com.my.myrpc.protocol.RpcMessage;
import com.my.myrpc.protocol.RpcRequest;
import com.my.myrpc.protocol.RpcResponse;
import com.my.myrpc.registry.zookeeper.ZookeeperServiceDiscovery;
import com.my.myrpc.serializer.SerializerFactory;
import com.my.myrpc.transport.client.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * RPC客户端动态代理
 * 使用JDK动态代理，将远程调用伪装成本地方法调用
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {
    
    private final ZookeeperServiceDiscovery serviceDiscovery;
    private final RpcClient rpcClient;
    
    public RpcClientProxy(String zkAddress) {
        this.serviceDiscovery = new ZookeeperServiceDiscovery(zkAddress);
        this.rpcClient = new RpcClient();
    }
    
    /**
     * 创建代理对象
     * @param clazz 接口类型
     * @param <T> 泛型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                this
        );
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 如果是Object类的方法，直接调用
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        
        // 构建RPC请求
        String requestId = UUID.randomUUID().toString();
        String interfaceName = method.getDeclaringClass().getName();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        
        RpcRequest request = RpcRequest.builder()
                .requestId(requestId)
                .interfaceName(interfaceName)
                .methodName(methodName)
                .parameterTypes(parameterTypes)
                .parameters(args)
                .version("1.0")
                .group("default")
                .build();
        
        log.info("发起RPC调用: {}.{}()", interfaceName, methodName);
        
        // 从注册中心获取服务地址
        String serviceAddress = serviceDiscovery.discoverOne(interfaceName, requestId);
        if (serviceAddress == null) {
            throw new RuntimeException("未找到服务: " + interfaceName);
        }
        
        // 解析服务地址
        String[] addressParts = serviceAddress.split(":");
        String host = addressParts[0];
        int port = Integer.parseInt(addressParts[1]);
        
        // 构建RPC消息
        RpcMessage message = RpcMessage.builder()
                .magicNumber(RpcConstants.MAGIC_NUMBER)
                .version(RpcConstants.VERSION)
                .serializerType(SerializerFactory.HESSIAN_SERIALIZER)
                .messageType(RpcConstants.REQUEST_TYPE)
                .data(request)
                .build();
        
        // 发送请求并等待响应
        rpcClient.sendRequest(host, port, message);
        
        // TODO: Day 3 实现异步等待响应
        // 使用CompletableFuture实现异步等待
        
        log.info("RPC调用完成: {}.{}()", interfaceName, methodName);
        return null;
    }
    
    public void close() {
        if (rpcClient != null) {
            rpcClient.close();
        }
        if (serviceDiscovery != null) {
            serviceDiscovery.close();
        }
    }
}
