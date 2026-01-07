package com.my.myrpc.registry;

import java.util.List;

/**
 * 服务发现接口
 */
public interface ServiceDiscovery {
    
    /**
     * 根据服务名称查找服务地址列表
     * @param serviceName 服务名称
     * @return 服务地址列表
     */
    List<String> discover(String serviceName);
}
