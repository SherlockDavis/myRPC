package com.my.myrpc.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡策略
 * 从服务列表中随机选择一个服务地址
 */
public class RandomLoadBalance implements LoadBalance {
    
    private final Random random = new Random();
    
    /**
     * 随机选择策略
     * @param serviceAddresses 服务地址列表
     * @param requestId 请求ID（随机策略不使用此参数）
     * @return 随机选中的服务地址
     */
    @Override
    public String select(List<String> serviceAddresses, String requestId) {
        if (serviceAddresses == null || serviceAddresses.isEmpty()) {
            return null;
        }
        
        // 生成随机索引，范围：[0, size)
        int index = random.nextInt(serviceAddresses.size());
        return serviceAddresses.get(index);
    }
}
