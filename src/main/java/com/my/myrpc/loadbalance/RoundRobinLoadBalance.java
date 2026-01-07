package com.my.myrpc.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡策略
 */
public class RoundRobinLoadBalance implements LoadBalance {
    
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    
    // TODO: Day 4 上午实现轮询负载均衡
    
    @Override
    public String select(List<String> serviceAddresses, String requestId) {
        if (serviceAddresses == null || serviceAddresses.isEmpty()) {
            return null;
        }
        int index = currentIndex.getAndIncrement() % serviceAddresses.size();
        return serviceAddresses.get(index);
    }
}
