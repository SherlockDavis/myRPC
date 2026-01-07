package com.my.myrpc.transport.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC客户端处理器
 * 负责接收服务端响应
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<String> {
    
    /**
     * 接收服务端响应
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        log.info("客户端收到响应: {}", msg);
        
        // TODO: Day 3 实现响应匹配逻辑
        // 1. 根据requestId匹配请求
        // 2. 将响应结果返回给调用方（通过CompletableFuture）
    }
    
    /**
     * 处理空闲状态事件（发送心跳）
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                log.debug("发送心跳包");
                // TODO: Day 4 发送心跳消息
                // ctx.writeAndFlush(heartbeatMessage);
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
        log.info("连接服务端成功: {}", ctx.channel().remoteAddress());
    }
    
    /**
     * 连接断开时触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("与服务端断开连接: {}", ctx.channel().remoteAddress());
        ctx.close();
    }
    
    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端处理异常: ", cause);
        ctx.close();
    }
}
