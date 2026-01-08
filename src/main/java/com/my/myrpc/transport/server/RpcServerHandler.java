package com.my.myrpc.transport.server;

import com.my.myrpc.common.constants.RpcConstants;
import com.my.myrpc.protocol.RpcMessage;
import com.my.myrpc.protocol.RpcRequest;
import com.my.myrpc.protocol.RpcResponse;
import com.my.myrpc.serializer.SerializerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * RPC服务端处理器
 * 负责处理客户端的RPC请求
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcMessage> {
    
    /**
     * 注册的本地服务
     * key: 服务接口全限定名
     * value: 服务实现对象
     */
    private static final ConcurrentMap<String, Object> services = new ConcurrentHashMap<>();
    
    public static void registerService(String serviceName, Object service) {
        services.put(serviceName, service);
        log.info("服务注册成功: {}", serviceName);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) {
        try {
            log.debug("服务端接收到RPC请求, 请求ID: {}", 
                    msg.getData() instanceof RpcRequest ? ((RpcRequest) msg.getData()).getRequestId() : "unknown");
            
            RpcMessage response = new RpcMessage();
            response.setMagicNumber(RpcConstants.MAGIC_NUMBER);
            response.setVersion(msg.getVersion());
            response.setSerializerType(msg.getSerializerType());
            
            // 处理请求消息
            if (msg.getData() instanceof RpcRequest) {
                RpcRequest request = (RpcRequest) msg.getData();
                response.setMessageType(RpcConstants.RESPONSE_TYPE);
                
                try {
                    // TODO: Day 3 实现反射调用业务逻辑
                    RpcResponse rpcResponse = invokeService(request);
                    response.setData(rpcResponse);
                } catch (Exception e) {
                    log.error("业务方法调用失败", e);
                    RpcResponse errorResponse = RpcResponse.fail(request.getRequestId(), e.getMessage());
                    response.setData(errorResponse);
                }
            } else {
                // 处理心跳消息
                response.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                response.setData("PONG");
            }
            
            ctx.writeAndFlush(response);
            log.debug("响应已发送");
        } catch (Exception e) {
            log.error("处理请求异常", e);
            ctx.close();
        }
    }
    
    /**
     * 通过反射调用本地服务
     */
    private RpcResponse invokeService(RpcRequest request) throws Exception {
        Object service = services.get(request.getInterfaceName());
        if (service == null) {
            throw new RuntimeException("找不到服务: " + request.getInterfaceName());
        }
        
        try {
            // TODO: 实现反射调用逻辑
            Object result = null;
            return RpcResponse.success(request.getRequestId(), result);
        } catch (Exception e) {
            throw new RuntimeException("服务调用失败", e);
        }
    }
    
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
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("客户端连接成功: {}", ctx.channel().remoteAddress());
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("客户端断开连接: {}", ctx.channel().remoteAddress());
        ctx.close();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务端处理异常: ", cause);
        ctx.close();
    }
}
