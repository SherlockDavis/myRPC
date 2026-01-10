package com.my.myrpc.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import com.my.myrpc.codec.RpcDecoder;
import com.my.myrpc.codec.RpcEncoder;
import com.my.myrpc.protocol.RpcMessage;
import com.my.myrpc.protocol.RpcRequest;
import com.my.myrpc.protocol.RpcResponse;
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
     * @return CompletableFuture<RpcResponse>
     */
    public CompletableFuture<RpcResponse> sendRequest(String host, int port, RpcMessage message) {
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        
        try {
            Channel channel = connect(host, port);
            
            if (channel != null && channel.isActive()) {
                // 获取requestId
                String requestId = null;
                if (message.getData() instanceof RpcRequest) {
                    requestId = ((RpcRequest) message.getData()).getRequestId();
                }
                
                // 注册Future
                if (requestId != null) {
                    clientHandler.getUnprocessedRequests().put(requestId, resultFuture);
                }
                
                // 发送请求
                final String finalRequestId = requestId;
                channel.writeAndFlush(message).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        log.info("请求发送成功: requestId={}", finalRequestId);
                    } else {
                        log.error("请求发送失败", future.cause());
                        resultFuture.completeExceptionally(future.cause());
                        if (finalRequestId != null) {
                            clientHandler.getUnprocessedRequests().remove(finalRequestId);
                        }
                    }
                });
            } else {
                resultFuture.completeExceptionally(new RuntimeException("连接失败"));
            }
        } catch (Exception e) {
            log.error("发送请求失败", e);
            resultFuture.completeExceptionally(e);
        }
        
        return resultFuture;
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
