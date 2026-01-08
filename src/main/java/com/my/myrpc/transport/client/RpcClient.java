package com.my.myrpc.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import com.my.myrpc.codec.RpcDecoder;
import com.my.myrpc.codec.RpcEncoder;
import com.my.myrpc.protocol.RpcMessage;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * RPC客户端
 * 基于Netty实现，负责发送请求并接收响应
 */
@Slf4j
public class RpcClient {
    
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final RpcClientHandler clientHandler;
    
    public RpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        clientHandler = new RpcClientHandler();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 心跳检测：15秒没有写操作则发送心跳
                        pipeline.addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcDecoder());
                        pipeline.addLast(new RpcEncoder());
                        // 业务处理器
                        pipeline.addLast(clientHandler);
                    }
                });
    }
    
    /**
     * 连接到服务端
     */
    private Channel connect(String host, int port) throws InterruptedException {
        ChannelFuture future = bootstrap.connect(host, port).sync();
        log.info("连接到服务端: {}:{}", host, port);
        return future.channel();
    }
    
    /**
     * 发送RPC请求
     * @param host 服务端地址
     * @param port 服务端端口
     * @param message RPC消息
     */
    public void sendRequest(String host, int port, Object message) {
        try {
            Channel channel = connect(host, port);
            
            if (channel != null && channel.isActive()) {
                // 发送请求
                channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        log.info("请求发送成功");
                    } else {
                        log.error("请求发送失败", future.cause());
                    }
                });
                
                // TODO: Day 3 实现异步等待响应
                // 使用CompletableFuture实现异步等待
                
                // 临时实现：等待一下让响应返回
                Thread.sleep(1000);
                channel.close();
            }
        } catch (Exception e) {
            log.error("发送请求失败", e);
        }
    }
    
    /**
     * 关闭客户端
     */
    public void close() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
        log.info("RPC客户端已关闭");
    }
}
