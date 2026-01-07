package com.my.myrpc.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * RPC服务端
 * 基于Netty实现，负责接收和处理客户端请求
 * 使用主从Reactor线程模型：Boss线程负责接收连接，Worker线程负责处理IO
 */
@Slf4j
public class RpcServer {
    
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    
    public RpcServer(int port) {
        this.port = port;
    }
    
    /**
     * 启动RPC服务端
     */
    public void start() {
        // Boss线程组：负责接收客户端连接
        bossGroup = new NioEventLoopGroup(1);
        // Worker线程组：负责处理IO操作
        workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP参数：保持长连接
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 日志处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 子处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 空闲状态检测：30秒没有读操作则触发事件
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            // 临时使用字符串编解码器（Day 2会替换为自定义协议）
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            // 业务处理器
                            pipeline.addLast(new RpcServerHandler());
                        }
                    });
            
            // 绑定端口并启动
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("RPC服务端启动成功，监听端口: {}", port);
            
            serverChannel = future.channel();
            // 等待服务端关闭
            serverChannel.closeFuture().sync();
            
        } catch (InterruptedException e) {
            log.error("RPC服务端启动失败", e);
            Thread.currentThread().interrupt();
        } finally {
            stop();
        }
    }
    
    /**
     * 停止RPC服务端
     */
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("RPC服务端已关闭");
    }
}
