package com.my.myrpc.codec;

import com.my.myrpc.common.constants.RpcConstants;
import com.my.myrpc.protocol.RpcMessage;
import com.my.myrpc.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC编码器
 * 将RpcMessage编码为字节流
 * 
 * 合议https://datatracker.ietf.org/doc/html/rfc5234提供一个序列化数据的二进制编码方案：
 * +-------+--------+----------+----------+----------+
 * | 魔数  | 版本号 |序列化| 消息类型 | 数据体简|
 * | 4字节 | 1字节 | 1字节  | 1字节   | 4字节   |
 * +-------+--------+----------+----------+----------+
 * | 实际数据
 * | N字节    
 * +----------
 */
@Slf4j
public class RpcEncoder extends MessageToByteEncoder<RpcMessage> {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) {
        try {
            // 1. 写入魔数
            out.writeInt(msg.getMagicNumber());
            
            // 2. 写入版本号
            out.writeByte(msg.getVersion());
            
            // 3. 写入序列化类型
            out.writeByte(msg.getSerializerType());
            
            // 4. 写入消息类型
            out.writeByte(msg.getMessageType());
            
            // 5. 序列化消息体
            byte[] data = SerializerFactory.getSerializer(msg.getSerializerType())
                    .serialize(msg.getData());
            
            // 6. 写入数据体长度
            out.writeInt(data.length);
            
            // 7. 写入消息体
            out.writeBytes(data);
            
            log.debug("编码RPC消息成功, 消息类型: {}, 数据长度: {}", 
                    msg.getMessageType(), data.length);
        } catch (Exception e) {
            log.error("编码RPC消息失败", e);
            ctx.close();
        }
    }
}
