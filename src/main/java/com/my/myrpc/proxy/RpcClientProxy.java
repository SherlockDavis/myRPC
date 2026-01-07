package com.my.myrpc.proxy;

/**
 * RPC客户端动态代理
 * 使用JDK动态代理，将远程调用伪装成本地方法调用
 */
public class RpcClientProxy {
    
    // TODO: Day 3 下午实现JDK动态代理
    
    /**
     * 创建代理对象
     * @param clazz 接口类型
     * @param <T> 泛型
     * @return 代理对象
     */
    public <T> T createProxy(Class<T> clazz) {
        // 使用Proxy.newProxyInstance创建代理
        // 在invoke方法中：
        // 1. 从注册中心获取服务地址
        // 2. 封装RpcRequest
        // 3. 通过RpcClient发送请求
        // 4. 返回结果
        return null;
    }
}
