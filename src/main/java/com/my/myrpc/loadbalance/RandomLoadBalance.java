package com.my.myrpc.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡策略
 */
public class RandomLoadBalance implements LoadBalance {
    
    private final Random random = new Random();
    
    // TODO: Day 4 上午实现随机负载均衡
    
    @Override
    public String select(List<String> serviceAddresses, String requestId) {
        if (serviceAddresses == null || serviceAddresses.isEmpty()) {
            return null;
        }
        int index = random.nextInt(serviceAddresses.size());
        return serviceAddresses.get(index);
    }
}
