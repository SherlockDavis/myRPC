package com.my.myrpc.transport.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC服务端处理器
 * 负责处理客户端的RPC请求
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<String> {
    
    /**
     * 处理接收到的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        log.info("服务端收到消息: {}", msg);
        
        // TODO: Day 2 添加协议解析
        // 1. 解析RpcRequest
        // 2. 通过反射调用本地服务
        // 3. 封装RpcResponse返回
        
        // 临时实现：回显消息
        ctx.writeAndFlush("Echo: " + msg);
    }
    
    /**
     * 处理空闲状态事件（心跳检测）
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.warn("客户端空闲超时，关闭连接: {}", ctx.channel().remoteAddress());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    
    /**
     * 连接建立时触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("客户端连接成功: {}", ctx.channel().remoteAddress());
    }
    
    /**
     * 连接断开时触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("客户端断开连接: {}", ctx.channel().remoteAddress());
        ctx.close();
    }
    
    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务端处理异常: ", cause);
        ctx.close();
    }
}
