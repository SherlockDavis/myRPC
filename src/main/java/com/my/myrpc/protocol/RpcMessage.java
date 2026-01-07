package com.my.myrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC消息协议
 * 协议格式：
 * +-------+--------+------------+--------+----------+
 * | 魔数  | 版本号 | 序列化算法 | 消息类型| 数据长度 | 消息体   |
 * | 4字节 | 1字节  | 1字节      | 1字节   | 4字节    | N字节    |
 * +-------+--------+------------+--------+----------+
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 魔数，用于识别协议
     */
    private int magicNumber;
    
    /**
     * 版本号
     */
    private byte version;
    
    /**
     * 序列化算法类型
     * 0: Java原生序列化
     * 1: Protobuf
     * 2: Hessian
     */
    private byte serializerType;
    
    /**
     * 消息类型
     */
    private byte messageType;
    
    /**
     * 数据长度
     */
    private int dataLength;
    
    /**
     * 消息体数据
     */
    private Object data;
}
