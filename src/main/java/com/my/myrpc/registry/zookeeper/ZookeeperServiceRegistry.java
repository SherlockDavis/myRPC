package com.my.myrpc.registry.zookeeper;

import com.my.myrpc.common.constants.RpcConstants;
import com.my.myrpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 基于Zookeeper的服务注册实现
 */
@Slf4j
public class ZookeeperServiceRegistry implements ServiceRegistry {
    
    private final CuratorFramework zkClient;
    
    public ZookeeperServiceRegistry(String zkAddress) {
        // 创建Zookeeper客户端
        this.zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .sessionTimeoutMs(RpcConstants.ZK_SESSION_TIMEOUT)
                .connectionTimeoutMs(RpcConstants.ZK_CONNECTION_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        this.zkClient.start();
        log.info("Zookeeper客户端启动成功，连接地址: {}", zkAddress);
    }
    
    @Override
    public void register(String serviceName, String serviceAddress) {
        try {
            // 服务路径: /myrpc/serviceName/serviceAddress
            String servicePath = RpcConstants.ZK_ROOT_PATH + "/" + serviceName;
            
            // 创建持久化服务节点（如果不存在）
            if (zkClient.checkExists().forPath(servicePath) == null) {
                zkClient.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath);
                log.info("创建服务节点: {}", servicePath);
            }
            
            // 创建临时服务地址节点
            String addressPath = servicePath + "/" + serviceAddress;
            if (zkClient.checkExists().forPath(addressPath) == null) {
                zkClient.create()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(addressPath);
                log.info("服务注册成功: {} -> {}", serviceName, serviceAddress);
            }
        } catch (Exception e) {
            log.error("服务注册失败: {} -> {}", serviceName, serviceAddress, e);
            throw new RuntimeException("服务注册失败", e);
        }
    }
    
    @Override
    public void unregister(String serviceName, String serviceAddress) {
        try {
            String addressPath = RpcConstants.ZK_ROOT_PATH + "/" + serviceName + "/" + serviceAddress;
            if (zkClient.checkExists().forPath(addressPath) != null) {
                zkClient.delete().forPath(addressPath);
                log.info("服务注销成功: {} -> {}", serviceName, serviceAddress);
            }
        } catch (Exception e) {
            log.error("服务注销失败: {} -> {}", serviceName, serviceAddress, e);
        }
    }
    
    public void close() {
        if (zkClient != null) {
            zkClient.close();
            log.info("Zookeeper客户端已关闭");
        }
    }
}
