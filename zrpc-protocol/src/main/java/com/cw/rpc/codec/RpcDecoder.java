package com.cw.rpc.codec;

import com.cw.rpc.core.RpcRequest;
import com.cw.rpc.core.RpcResponse;
import com.cw.rpc.protocol.MessageHeader;
import com.cw.rpc.protocol.RpcProtocol;
import com.cw.rpc.protocol.constants.RpcProtocolConstants;
import com.cw.rpc.protocol.rpc_enum.MessageTypeEnum;
import com.cw.rpc.serialization.RpcSerialization;
import com.cw.rpc.serialization.factory.RpcSerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author 小怪兽
 * @Date 2021-03-31
 * 解码器：将直接数组转换为自定义协议对象
 */
public class RpcDecoder extends ByteToMessageDecoder {

    /**
     * 1.做合法性的边界判断
     * 2.解析出字节流数据
     * 3.构建协议包对象
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //1.边界判断,是否为一个合法消息
        if (in.readableBytes() < RpcProtocolConstants.HEADER_TOTAL_LENGTH) {
            return;
        }
        //2.记录当前的读索引位置
        in.markReaderIndex();
        //3.判断魔数是否合法
        short magic = in.readShort();
        if (magic != RpcProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic is illegal" + magic);
        }
        //4.继续读取协议头其他信息
        byte version = in.readByte();
        byte serialization = in.readByte();
        byte msgType = in.readByte();
        //5.判断消息类型是否合法
        MessageTypeEnum msgTypeEnum = MessageTypeEnum.findByType(msgType);
        if (msgTypeEnum == null) {
            in.resetReaderIndex();
            throw new IllegalArgumentException("msgType is illegal" + msgType);
        }
        byte status = in.readByte();
        long msgId = in.readLong();
        int msgLength = in.readInt();
        //6.判断消息是否传输完毕
        if (in.readableBytes() < msgLength) {
            in.resetReaderIndex();
            return;
        }
        //7.读取消息体
        byte[] bytes = new byte[msgLength];
        in.readBytes(bytes);

        //8.完成数据的读取之后
        //构建成一个协议头对象
        MessageHeader header = new MessageHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerialization(serialization);
        header.setMsgType(msgType);
        header.setStatus(status);
        header.setMsgId(msgId);
        header.setMsgLength(msgLength);

        //构建一个协议体对象
        RpcSerialization rpcSerialization = RpcSerializationFactory.getRpcSerialization(serialization);
        switch (msgTypeEnum) {
            case REQUEST:
                //发送的是请求包
                RpcRequest request = rpcSerialization.deserialization(bytes, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPOSE:
                //发送的是响应包
                RpcResponse response = rpcSerialization.deserialization(bytes, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            //拓展：心跳包
        }
    }
}
