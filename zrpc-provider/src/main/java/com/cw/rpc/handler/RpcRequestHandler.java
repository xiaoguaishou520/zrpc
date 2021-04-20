package com.cw.rpc.handler;

import com.cw.rpc.cache.LocalCache;
import com.cw.rpc.core.RpcRequest;
import com.cw.rpc.core.RpcResponse;
import com.cw.rpc.core.RpcUtils;
import com.cw.rpc.processor.RpcRequestProcessor;
import com.cw.rpc.protocol.MessageHeader;
import com.cw.rpc.protocol.RpcProtocol;
import com.cw.rpc.protocol.rpc_enum.MessageStatusEnum;
import com.cw.rpc.protocol.rpc_enum.MessageTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author 小怪兽
 * @Date 2021-04-01
 * 服务端接收请求逻辑处理器
 */
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> requestRpcProtocol) throws Exception {
        //将业务操作交给业务线程池来处理
        RpcRequestProcessor.submit(()->{
            //1.创建一个返回给客户端的协议包对象
            RpcProtocol<RpcResponse> rpcProtocol = new RpcProtocol<>();
            //2.创建响应体对象
            RpcResponse response = new RpcResponse();
            //3.设置响应头的信息(复用请求的协议信息，修改部分字段即可)
            MessageHeader header = requestRpcProtocol.getHeader();
            header.setMsgType((byte) MessageTypeEnum.RESPOSE.getType());
            //4.获取客户端发送的请求信息
            RpcRequest rpcRequest = requestRpcProtocol.getBody();
            try {
                //5.根据请求的信息，调用对应的服务方法，并返回结果
                Object result = handleRequest(rpcRequest);
                //6.设置响应结果
                response.setData(result);
                header.setStatus((byte) MessageStatusEnum.SUCCESS.getType());
                //7.设置响应的协议对象
                rpcProtocol.setHeader(header);
                rpcProtocol.setBody(response);
            } catch (Throwable throwable) {
                header.setStatus((byte) MessageStatusEnum.FAIL.getType());
                log.error("RpcRequestHanlder process request {} fail",header.getMsgId());
            }
            //给客户端反馈处理结果
            ctx.writeAndFlush(rpcProtocol);
        });
    }

    /**
     * 根据请求的信息，调用对应的服务方法，并返回结果
     * @param rpcRequest
     * @return
     */
    private Object handleRequest(RpcRequest rpcRequest) throws InvocationTargetException {
        //根据请求的信息定位到具体方法
        //1.构建服务的key
        String serviceKey = RpcUtils.buildSeviceKey(rpcRequest.getClassName(), rpcRequest.getVersion());
        //2.从本地缓存获取服务
        Object serviceBean = LocalCache.rpcServiceMap.get(serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(
                    "service not exist:" + rpcRequest.getClassName() +
                            ",method:" + rpcRequest.getMethodName());
        }
        //3.采用Cglib提供的FastClass方法来实现方法的调用
        //3.1 动态生成一个继承FastClass的类，并向这个类注入委托对象
        FastClass fastClass = FastClass.create(serviceBean.getClass());
        //3.2 通过索引的方式定位到具体的方法
        int index = fastClass.getIndex(rpcRequest.getMethodName(), rpcRequest.getParamterTypes());
        //3.3 根据索引调用目标方法
        return fastClass.invoke(index,serviceBean,rpcRequest.getParams());
    }
}
