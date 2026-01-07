package com.my.myrpc.loadbalance;

import java.util.List;

/**
 * 负载均衡接口
 */
public interface LoadBalance {
    
    /**
     * 从服务地址列表中选择一个
     * @param serviceAddresses 服务地址列表
     * @param requestId 请求ID（用于一致性哈希等算法）
     * @return 选中的服务地址
     */
    String select(List<String> serviceAddresses, String requestId);
}
