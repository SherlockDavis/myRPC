package com.my.myrpc.transport.client;

import com.my.myrpc.protocol.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理的请求管理器
 * 用于管理请求和响应的异步匹配
 */
public class UnprocessedRequests {
    
    /**
     * 未完成的请求
     * key: requestId
     * value: CompletableFuture
     */
    private static final Map<String, CompletableFuture<RpcResponse>> UNPROCESSED_REQUESTS = new ConcurrentHashMap<>();
    
    /**
     * 添加未处理的请求
     */
    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        UNPROCESSED_REQUESTS.put(requestId, future);
    }
    
    /**
     * 完成请求
     */
    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = UNPROCESSED_REQUESTS.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        }
    }
    
    /**
     * 移除请求
     */
    public void remove(String requestId) {
        UNPROCESSED_REQUESTS.remove(requestId);
    }
}
