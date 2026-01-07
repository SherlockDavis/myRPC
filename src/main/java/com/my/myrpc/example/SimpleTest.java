package com.my.myrpc.example;

import com.my.myrpc.transport.client.RpcClient;
import com.my.myrpc.transport.server.RpcServer;
import lombok.extern.slf4j.Slf4j;

/**
 * 简单测试类
 * 测试Netty服务端和客户端的基本通信
 */
@Slf4j
public class SimpleTest {
    
    public static void main(String[] args) {
        // 启动服务端（在新线程中）
        new Thread(() -> {
            RpcServer server = new RpcServer(8888);
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
        client.sendRequest("localhost", 8888, "Hello, RPC Server!");
        
        // 等待一下
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        client.close();
        log.info("测试完成");
    }
}
