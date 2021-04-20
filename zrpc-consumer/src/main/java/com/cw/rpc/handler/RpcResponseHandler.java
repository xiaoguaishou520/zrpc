package com.cw.rpc.handler;

import com.cw.rpc.consumer.RpcRequestHolder;
import com.cw.rpc.core.RpcFuture;
import com.cw.rpc.core.RpcResponse;
import com.cw.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author 小怪兽
 * @Date 2021-04-02
 * 消费者请求之后接收提供者响应的处理器
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) throws Exception {
        //接收到服务端的处理结果之后，将其结果设置到消息对应的future中
        long msgId = protocol.getHeader().getMsgId();
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.get(msgId);
        future.getPromise().setSuccess(protocol.getBody());
    }
}
