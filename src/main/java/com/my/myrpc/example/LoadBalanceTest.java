package com.my.myrpc.example;

import com.my.myrpc.loadbalance.LoadBalance;
import com.my.myrpc.loadbalance.RandomLoadBalance;
import com.my.myrpc.loadbalance.RoundRobinLoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负载均衡器测试类
 */
@Slf4j
public class LoadBalanceTest {
    
    public static void main(String[] args) {
        log.info("================ 负载均衡器测试 ================");
        
        // 创建服务地址列表
        List<String> serviceAddresses = Arrays.asList(
            "192.168.1.100:8080",
            "192.168.1.101:8080", 
            "192.168.1.102:8080",
            "192.168.1.103:8080"
        );
        
        log.info("服务地址列表: {}", serviceAddresses);
        
        // 测试随机负载均衡
        testRandomLoadBalance(serviceAddresses);
        
        // 测试轮询负载均衡
        testRoundRobinLoadBalance(serviceAddresses);
        
        log.info("================ 测试完成 ================");
    }
    
    /**
     * 测试随机负载均衡
     */
    private static void testRandomLoadBalance(List<String> serviceAddresses) {
        log.info("\n-------------- 随机负载均衡测试 --------------");
        
        LoadBalance randomLB = new RandomLoadBalance();
        Map<String, Integer> countMap = new ConcurrentHashMap<>();
        
        // 执行100次请求
        for (int i = 0; i < 100; i++) {
            String selected = randomLB.select(serviceAddresses, "req-" + i);
            countMap.merge(selected, 1, Integer::sum);
        }
        
        log.info("随机负载均衡结果统计:");
        countMap.forEach((address, count) -> 
            log.info("  {}: {}次", address, count)
        );
        
        // 验证所有服务都被选中
        if (countMap.size() == serviceAddresses.size()) {
            log.info("✅ 随机负载均衡测试通过: 所有服务节点都被访问到");
        } else {
            log.warn("⚠️ 随机负载均衡测试警告: 部分服务节点未被访问到");
        }
    }
    
    /**
     * 测试轮询负载均衡
     */
    private static void testRoundRobinLoadBalance(List<String> serviceAddresses) {
        log.info("\n-------------- 轮询负载均衡测试 --------------");
        
        LoadBalance roundRobinLB = new RoundRobinLoadBalance();
        Map<String, Integer> countMap = new ConcurrentHashMap<>();
        
        // 执行100次请求
        for (int i = 0; i < 100; i++) {
            String selected = roundRobinLB.select(serviceAddresses, "req-" + i);
            countMap.merge(selected, 1, Integer::sum);
        }
        
        log.info("轮询负载均衡结果统计:");
        countMap.forEach((address, count) -> 
            log.info("  {}: {}次", address, count)
        );
        
        // 验证请求分布是否均匀
        boolean isBalanced = countMap.values().stream()
            .allMatch(count -> Math.abs(count - 25) <= 1); // 100/4 = 25
        
        if (isBalanced) {
            log.info("✅ 轮询负载均衡测试通过: 请求分布均匀");
        } else {
            log.warn("⚠️ 轮询负载均衡测试警告: 请求分布不够均匀");
        }
        
        // 验证轮询顺序（前4次应该依次访问不同的服务）
        log.info("验证轮询顺序:");
        LoadBalance lb = new RoundRobinLoadBalance(); // 新实例确保从0开始
        for (int i = 0; i < 8; i++) { // 显示两轮
            String selected = lb.select(serviceAddresses, "req-" + i);
            log.info("  第{}次请求: {}", i + 1, selected);
        }
    }
}