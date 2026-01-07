package com.my.myrpc.protocol;

/**
 * 消息类型枚举
 */
public enum MessageType {
    
    /**
     * 请求消息
     */
    REQUEST((byte) 1),
    
    /**
     * 响应消息
     */
    RESPONSE((byte) 2),
    
    /**
     * 心跳请求
     */
    HEARTBEAT_REQUEST((byte) 3),
    
    /**
     * 心跳响应
     */
    HEARTBEAT_RESPONSE((byte) 4);
    
    private final byte type;
    
    MessageType(byte type) {
        this.type = type;
    }
    
    public byte getType() {
        return type;
    }
    
    public static MessageType parseByType(byte type) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getType() == type) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("未知的消息类型: " + type);
    }
}
