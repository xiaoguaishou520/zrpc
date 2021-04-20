package com.cw.rpc.codec;

import com.cw.rpc.protocol.MessageHeader;
import com.cw.rpc.protocol.RpcProtocol;
import com.cw.rpc.serialization.RpcSerialization;
import com.cw.rpc.serialization.factory.RpcSerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author 小怪兽
 * @Date 2021-03-31
 * 编码器：实现将自定义协议包转换为字节流
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {


    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf out) throws Exception {
        //1.从协议包获取协议头对象
        MessageHeader header = msg.getHeader();
        //2.将协议头信息写入ByteBuf
        out.writeShort(header.getMagic());
        out.writeByte(header.getVersion());
        out.writeByte(header.getSerialization());
        out.writeByte(header.getMsgType());
        out.writeByte(header.getStatus());
        out.writeLong(header.getMsgId());
        //3.将协议体对象转换为字节数组，消息长度有消息体决定
        //由协议头的serialization字段决定序列化类型
        RpcSerialization rpcSerialization = RpcSerializationFactory.getRpcSerialization(header.getSerialization());
        byte[] data = rpcSerialization.serialization(msg.getBody());
        //4.将消息长度和协议体写入ByteBuf
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
