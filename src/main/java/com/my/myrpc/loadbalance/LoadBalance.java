package com.my.myrpc.loadbalance;

import java.util.List;

/**
 * 负载均衡接口
 * 定义服务地址选择策略
 * 
 * 支持多种负载均衡算法：
 * - 随机选择（RandomLoadBalance）
 * - 轮询选择（RoundRobinLoadBalance）
 * - 加权轮询（WeightedRoundRobinLoadBalance）
 * - 一致性哈希（ConsistentHashLoadBalance）
 * - 最少连接（LeastConnectionsLoadBalance）
 */
public interface LoadBalance {
    
    /**
     * 从服务地址列表中选择一个
     * 
     * @param serviceAddresses 服务地址列表，格式：["host1:port1", "host2:port2", ...]
     * @param requestId 请求ID，可用于一致性哈希等需要上下文信息的算法
     * @return 选中的服务地址，格式："host:port"，如果列表为空则返回null
     */
    String select(List<String> serviceAddresses, String requestId);
}
