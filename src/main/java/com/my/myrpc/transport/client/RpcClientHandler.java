package com.my.myrpc.transport.client;

import com.my.myrpc.common.constants.RpcConstants;
import com.my.myrpc.protocol.RpcMessage;
import com.my.myrpc.protocol.RpcResponse;
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
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcMessage> {
    
    private final UnprocessedRequests unprocessedRequests;
    
    public RpcClientHandler() {
        this.unprocessedRequests = new UnprocessedRequests();
    }
    
    public UnprocessedRequests getUnprocessedRequests() {
        return unprocessedRequests;
    }
    
    /**
     * 接收服务端响应
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) {
        log.debug("客户端收到响应");
        
        // 处理响应消息
        if (msg.getMessageType() == RpcConstants.RESPONSE_TYPE) {
            RpcResponse rpcResponse = (RpcResponse) msg.getData();
            log.info("收到RPC响应: requestId={}, code={}", 
                    rpcResponse.getRequestId(), rpcResponse.getCode());
            
            // 完成对应的Future
            unprocessedRequests.complete(rpcResponse);
        } else if (msg.getMessageType() == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            log.debug("收到心跳响应: {}", msg.getData());
        }
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
                // 发送心跳消息
                RpcMessage heartbeat = RpcMessage.builder()
                        .magicNumber(RpcConstants.MAGIC_NUMBER)
                        .version(RpcConstants.VERSION)
                        .serializerType((byte) 0)
                        .messageType(RpcConstants.HEARTBEAT_REQUEST_TYPE)
                        .data("PING")
                        .build();
                ctx.writeAndFlush(heartbeat);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("连接服务端成功: {}", ctx.channel().remoteAddress());
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("与服务端断开连接: {}", ctx.channel().remoteAddress());
        ctx.close();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端处理异常: ", cause);
        ctx.close();
    }
}
