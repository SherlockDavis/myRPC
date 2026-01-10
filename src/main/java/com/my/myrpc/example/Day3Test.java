package com.my.myrpc.example;

import com.my.myrpc.common.constants.RpcConstants;
import com.my.myrpc.example.api.HelloService;
import com.my.myrpc.example.provider.HelloServiceImpl;
import com.my.myrpc.protocol.RpcMessage;
import com.my.myrpc.protocol.RpcRequest;
import com.my.myrpc.protocol.RpcResponse;
import com.my.myrpc.serializer.SerializerFactory;
import com.my.myrpc.transport.client.RpcClient;
import com.my.myrpc.transport.server.RpcServer;
import com.my.myrpc.transport.server.RpcServerHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Day 3 测试类
 * 测试反射调用功能
 */
@Slf4j
public class Day3Test {
    
    public static void main(String[] args) {
        log.info("================ Day 3 测试开始 ================");
        
        // 启动服务端（在新线程中）
        new Thread(() -> {
            RpcServer server = new RpcServer(8888);
            // 注册测试服务
            RpcServerHandler.registerService(
                "com.my.myrpc.example.api.HelloService",
                new HelloServiceImpl()
            );
            server.start();
        }).start();
        
        // 等待服务端启动
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 启动客户端并发送请求
        RpcClient client = new RpcClient();
        
        // 构造RPC请求
        String requestId = UUID.randomUUID().toString();
        RpcRequest request = RpcRequest.builder()
                .requestId(requestId)
                .interfaceName("com.my.myrpc.example.api.HelloService")
                .methodName("sayHello")
                .parameterTypes(new Class<?>[]{String.class})
                .parameters(new Object[]{"RPC Framework"})
                .version("1.0")
                .group("default")
                .build();
        
        // 构造RPC消息并发送
        RpcMessage message = RpcMessage.builder()
                .magicNumber(RpcConstants.MAGIC_NUMBER)
                .version(RpcConstants.VERSION)
                .serializerType(SerializerFactory.HESSIAN_SERIALIZER)
                .messageType(RpcConstants.REQUEST_TYPE)
                .data(request)
                .build();
        
        log.info("\n============== 测试异步响应 ==============");
        log.info("请求ID: {}", requestId);
        log.info("调用方法: HelloService.sayHello(\"RPC Framework\")");
        
        CompletableFuture<RpcResponse> future = client.sendRequest("localhost", 8888, message);
        
        // 等待响应
        try {
            RpcResponse response = future.get(5, java.util.concurrent.TimeUnit.SECONDS);
            log.info("\n============== 响应结果 ==============");
            log.info("状态码: {}", response.getCode());
            log.info("响应消息: {}", response.getMessage());
            log.info("返回数据: {}", response.getData());
        } catch (Exception e) {
            log.error("等待响应失败", e);
        }
        
        // 等待一下
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        client.close();
        log.info("\n================ Day 3 测试结束 ================");
    }
}
