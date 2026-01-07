package com.my.myrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC响应消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 请求ID，与RpcRequest的requestId对应
     */
    private String requestId;
    
    /**
     * 响应状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private Object data;
    
    /**
     * 成功响应
     */
    public static RpcResponse success(String requestId, Object data) {
        return RpcResponse.builder()
                .requestId(requestId)
                .code(200)
                .message("success")
                .data(data)
                .build();
    }
    
    /**
     * 失败响应
     */
    public static RpcResponse fail(String requestId, String message) {
        return RpcResponse.builder()
                .requestId(requestId)
                .code(500)
                .message(message)
                .build();
    }
}
