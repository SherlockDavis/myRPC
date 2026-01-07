package com.my.myrpc.registry.zookeeper;

import com.my.myrpc.registry.ServiceRegistry;

/**
 * 基于Zookeeper的服务注册实现
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {
    
    // TODO: Day 3 实现Zookeeper服务注册
    
    @Override
    public void register(String serviceName, String serviceAddress) {
        // 实现服务注册逻辑
    }
    
    @Override
    public void unregister(String serviceName, String serviceAddress) {
        // 实现服务注销逻辑
    }
}
