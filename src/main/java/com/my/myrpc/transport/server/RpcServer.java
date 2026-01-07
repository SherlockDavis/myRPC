package com.my.myrpc.transport.server;

/**
 * RPC服务端
 * 基于Netty实现，负责接收和处理客户端请求
 */
public class RpcServer {
    
    // TODO: Day 1 下午实现Netty服务端启动逻辑
    
    /**
     * 启动RPC服务端
     * @param port 监听端口
     */
    public void start(int port) {
        // 配置主从Reactor线程模型
        // 添加编解码器和业务处理器到Pipeline
    }
    
    /**
     * 停止RPC服务端
     */
    public void stop() {
        // 优雅关闭
    }
}
