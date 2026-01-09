package com.my.myrpc.registry.zookeeper;

import com.my.myrpc.common.constants.RpcConstants;
import com.my.myrpc.loadbalance.LoadBalance;
import com.my.myrpc.loadbalance.RandomLoadBalance;
import com.my.myrpc.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

/**
 * 基于Zookeeper的服务发现实现
 */
@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
    
    private final CuratorFramework zkClient;
    private final LoadBalance loadBalance;
    
    public ZookeeperServiceDiscovery(String zkAddress) {
        // 创建Zookeeper客户端
        this.zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .sessionTimeoutMs(RpcConstants.ZK_SESSION_TIMEOUT)
                .connectionTimeoutMs(RpcConstants.ZK_CONNECTION_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        this.zkClient.start();
        this.loadBalance = new RandomLoadBalance();
        log.info("Zookeeper服务发现客户端启动成功，连接地址: {}", zkAddress);
    }
    
    @Override
    public List<String> discover(String serviceName) {
        try {
            String servicePath = RpcConstants.ZK_ROOT_PATH + "/" + serviceName;
            
            // 获取服务地址列表
            List<String> serviceAddresses = zkClient.getChildren().forPath(servicePath);
            
            if (serviceAddresses.isEmpty()) {
                log.warn("未找到服务: {}", serviceName);
                return null;
            }
            
            log.info("发现服务: {} -> {}", serviceName, serviceAddresses);
            return serviceAddresses;
        } catch (Exception e) {
            log.error("服务发现失败: {}", serviceName, e);
            return null;
        }
    }
    
    /**
     * 使用负载均衡策略选择一个服务地址
     */
    public String discoverOne(String serviceName, String requestId) {
        List<String> serviceAddresses = discover(serviceName);
        if (serviceAddresses == null || serviceAddresses.isEmpty()) {
            return null;
        }
        return loadBalance.select(serviceAddresses, requestId);
    }
    
    public void close() {
        if (zkClient != null) {
            zkClient.close();
            log.info("Zookeeper服务发现客户端已关闭");
        }
    }
}
