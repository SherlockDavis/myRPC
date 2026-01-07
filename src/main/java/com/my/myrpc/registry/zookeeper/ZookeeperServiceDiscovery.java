package com.my.myrpc.registry.zookeeper;

import com.my.myrpc.registry.ServiceDiscovery;

import java.util.List;

/**
 * 基于Zookeeper的服务发现实现
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    
    // TODO: Day 3 实现Zookeeper服务发现
    
    @Override
    public List<String> discover(String serviceName) {
        // 实现服务发现逻辑
        return null;
    }
}
