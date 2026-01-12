package com.my.myrpc.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡策略（Round Robin）
 * 按顺序依次选择服务地址，保证每个服务被均匀调用
 */
public class RoundRobinLoadBalance implements LoadBalance {
    
    /**
     * 当前索引（线程安全）
     * 使用AtomicInteger保证多线程环境下的原子性操作
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    
    /**
     * 轮询选择策略
     * @param serviceAddresses 服务地址列表
     * @param requestId 请求ID（轮询策略不使用此参数）
     * @return 按轮询顺序选中的服务地址
     */
    @Override
    public String select(List<String> serviceAddresses, String requestId) {
        if (serviceAddresses == null || serviceAddresses.isEmpty()) {
            return null;
        }
        
        // 获取当前索引并自增，然后对服务数量取模
        // getAndIncrement() 是原子操作，保证线程安全
        int index = currentIndex.getAndIncrement() % serviceAddresses.size();
        
        // 防止索引溢出后变成负数
        if (index < 0) {
            index = Math.abs(index);
        }
        
        return serviceAddresses.get(index);
    }
}
