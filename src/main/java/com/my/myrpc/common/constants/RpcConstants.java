package com.my.myrpc.common.constants;

/**
 * RPC框架常量定义
 */
public class RpcConstants {
    
    /**
     * 协议魔数，用于识别RPC协议数据包
     */
    public static final int MAGIC_NUMBER = 0xCAFEBABE;
    
    /**
     * 协议版本号
     */
    public static final byte VERSION = 1;
    
    /**
     * 请求类型
     */
    public static final byte REQUEST_TYPE = 1;
    
    /**
     * 响应类型
     */
    public static final byte RESPONSE_TYPE = 2;
    
    /**
     * 心跳请求类型
     */
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    
    /**
     * 心跳响应类型
     */
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    
    /**
     * 最大帧长度（10MB）
     */
    public static final int MAX_FRAME_LENGTH = 10 * 1024 * 1024;
    
    /**
     * Zookeeper根路径
     */
    public static final String ZK_ROOT_PATH = "/myrpc";
    
    /**
     * Zookeeper会话超时时间（毫秒）
     */
    public static final int ZK_SESSION_TIMEOUT = 5000;
    
    /**
     * Zookeeper连接超时时间（毫秒）
     */
    public static final int ZK_CONNECTION_TIMEOUT = 5000;
}
