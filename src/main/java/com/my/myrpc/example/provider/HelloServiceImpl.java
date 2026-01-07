package com.my.myrpc.example.provider;

import com.my.myrpc.annotation.RpcService;
import com.my.myrpc.example.api.HelloService;

/**
 * HelloService服务提供者实现
 */
@RpcService
public class HelloServiceImpl implements HelloService {
    
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + "! This is RPC response.";
    }
}
