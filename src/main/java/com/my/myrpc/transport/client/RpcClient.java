package com.my.myrpc.transport.client;

/**
 * RPC客户端
 * 基于Netty实现，负责发送请求并接收响应
 */
public class RpcClient {
    
    // TODO: Day 1 下午实现Netty客户端启动逻辑
    
    /**
     * 发送RPC请求
     * @param host 服务端地址
     * @param port 服务端端口
     * @param request 请求对象
     * @return 响应对象
     */
    public Object sendRequest(String host, int port, Object request) {
        // 建立连接
        // 发送请求
        // 等待响应
        return null;
    }
    
    /**
     * 关闭客户端
     */
    public void close() {
        // 释放资源
    }
}
