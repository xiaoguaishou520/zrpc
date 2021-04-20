package com.cw.rpc.consumer;

import com.cw.rpc.core.RegistryService;
import com.cw.rpc.core.RpcFuture;
import com.cw.rpc.core.RpcRequest;
import com.cw.rpc.core.RpcResponse;
import com.cw.rpc.protocol.MessageHeader;
import com.cw.rpc.protocol.RpcProtocol;
import com.cw.rpc.protocol.constants.RpcProtocolConstants;
import com.cw.rpc.protocol.rpc_enum.MessageStatusEnum;
import com.cw.rpc.protocol.rpc_enum.MessageTypeEnum;
import com.cw.rpc.protocol.rpc_enum.RpcSerializationTypeEnum;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @Author 小怪兽
 * @Date 2021-04-02
 */
public class RpcInvokerProxy implements InvocationHandler {

    private RegistryService registryService;
    private String version;
    private long timeout;

    public RpcInvokerProxy(RegistryService registryService, String version, long timeout) {
        this.registryService = registryService;
        this.version = version;
        this.timeout = timeout;
    }

    /**
     * 实现代理的处理逻辑
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1.构建自定义协议包
        RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
        //2.设置消息头
        MessageHeader header = new MessageHeader();
        header.setStatus((byte) MessageStatusEnum.SUCCESS.getType());
        header.setMsgType((byte) MessageTypeEnum.REQUEST.getType());
        header.setMsgId(RpcRequestHolder.ID_GENERATOR.incrementAndGet());//自增
        header.setSerialization((byte) RpcSerializationTypeEnum.HESSIAN.getType());
        header.setVersion(RpcProtocolConstants.VERSION);
        header.setMagic(RpcProtocolConstants.MAGIC);

//        header.setMsgLength();
        //3.构建消息体
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParamterTypes(method.getParameterTypes());
        request.setParams(args);
        request.setVersion(this.version);

        //4.设置消息头和消息体
        rpcProtocol.setHeader(header);
        rpcProtocol.setBody(request);

        //5.利用Consumer完成服务的调用
        RpcConsumer rpcConsumer = new RpcConsumer();
        rpcConsumer.sentRequest(rpcProtocol,registryService);
        //6.futrue 异步保存响应结果
        RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(
                new DefaultEventLoop()),
                timeout);
        RpcRequestHolder.REQUEST_MAP.put(header.getMsgId(),future);

        //7.等待结果
        return future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();
    }
}
