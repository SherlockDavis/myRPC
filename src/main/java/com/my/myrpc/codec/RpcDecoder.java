package com.my.myrpc.codec;

import com.my.myrpc.common.constants.RpcConstants;
import com.my.myrpc.protocol.RpcMessage;
import com.my.myrpc.serializer.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC解码器
 * 将字节流解码为RpcMessage
 * 
 * 使用LengthFieldBasedFrameDecoder解决TCP粘包/拖包问题：
 * 根据数据体长度字段预告下一个整整消息的余下秘淵长度
 */
@Slf4j
public class RpcDecoder extends LengthFieldBasedFrameDecoder {
    
    /**
     * LengthFieldBasedFrameDecoder参数说明：
     * maxFrameLength: 一个整整消息的最大锻叨黎
     * lengthFieldOffset: 频䷕长度字段的位置弗接 (4+1+1+1 = 7)
     * lengthFieldLength: 频䷕长度字段本身体增丰 (4字节)
     * lengthAdjustment: 频䷕长度序序中佋轮的惨段斯 (0)
     * initialBytesToStrip: 装野时丢弃的字节数 (7 + 4 = 11)
     */
    public RpcDecoder() {
        super(
            RpcConstants.MAX_FRAME_LENGTH,  // 最大消息长度
            7,                               // 频䷕长度字段位置 (4+1+1+1)
            4,                               // 频䷕长度字段本身体增丰
            0,                               // 频䷕长度调整
            11                               // 丢弃的字节数 (魔数+版本+序列类型+消息类型+长度)
        );
    }
    
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 父类处理消息边界提取
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        
        try {
            RpcMessage message = new RpcMessage();
            
            // 1. 解码魔数
            int magicNumber = frame.readInt();
            if (magicNumber != RpcConstants.MAGIC_NUMBER) {
                log.error("不合法的RPC消息，魔数不匹配");
                throw new RuntimeException("不合法的魔数: " + magicNumber);
            }
            message.setMagicNumber(magicNumber);
            
            // 2. 解码版本号
            byte version = frame.readByte();
            message.setVersion(version);
            
            // 3. 解码序列化类型
            byte serializerType = frame.readByte();
            message.setSerializerType(serializerType);
            
            // 4. 解码消息类型
            byte messageType = frame.readByte();
            message.setMessageType(messageType);
            
            // 5. 解码数据体长度
            int dataLength = frame.readInt();
            message.setDataLength(dataLength);
            
            // 6. 解码数据体
            byte[] data = new byte[dataLength];
            frame.readBytes(data);
            
            // 7. 反序列化数据
            Object obj = SerializerFactory.getSerializer(serializerType).deserialize(data, Object.class);
            message.setData(obj);
            
            log.debug("解码RPC消息成功, 消息类型: {}, 数据长度: {}", messageType, dataLength);
            
            return message;
        } finally {
            frame.release();
        }
    }
}
