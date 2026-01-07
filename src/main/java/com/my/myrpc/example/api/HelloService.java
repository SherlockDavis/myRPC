package com.my.myrpc.example.api;

/**
 * 示例服务接口
 * 用于演示RPC调用
 */
public interface HelloService {
    
    /**
     * 问候方法
     * @param name 名字
     * @return 问候语
     */
    String sayHello(String name);
}
