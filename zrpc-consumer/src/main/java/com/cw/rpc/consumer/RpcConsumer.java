package com.cw.rpc.consumer;

import com.cw.rpc.codec.RpcDecoder;
import com.cw.rpc.codec.RpcEncoder;
import com.cw.rpc.core.RegistryService;
import com.cw.rpc.core.RpcRequest;
import com.cw.rpc.core.RpcUtils;
import com.cw.rpc.core.ServiceMeta;
import com.cw.rpc.handler.RpcResponseHandler;
import com.cw.rpc.protocol.RpcProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author 小怪兽
 * @Date 2021-04-02
 * 基于Netty实现远程的调用
 */
@Slf4j
public class RpcConsumer {

    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    public RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加自定义编解码器
                        pipeline.addLast(new RpcEncoder());
                        pipeline.addLast(new RpcDecoder());
                        //添加业务处理的handler
                        pipeline.addLast(new RpcResponseHandler());
                    }
                });
    }

    /**
     * 发送请求信息
     * @param rpcProtocol
     * @param registryService
     */
    public void sentRequest(RpcProtocol<RpcRequest> rpcProtocol, RegistryService registryService) throws Exception {
        //1.通过注册中心获取服务的元信息
        RpcRequest request = rpcProtocol.getBody();
        String seviceKey = RpcUtils.buildSeviceKey(request.getClassName(), request.getVersion());
        //2.获取到客户端的hashcode，基于客户端的请求参数值来计算
        Object[] params = request.getParams();
        int hashcode = params != null ? params.hashCode() : seviceKey.hashCode();
        //3.通过注册中心操作对象，获取信息
        ServiceMeta serviceMeta = registryService.discovery(seviceKey, hashcode);
        //4.根据服务元数据信息建立连接
        if (serviceMeta != null) {
            ChannelFuture future = bootstrap.connect(
                    serviceMeta.getServiceAddr(), serviceMeta.getServicePort()).sync();
            //添加监听器来监控是否连接成功
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("connect remote server {} on port {} success",
                                serviceMeta.getServiceName(), serviceMeta.getServicePort());
                    } else {
                        log.error("connect remote server {} on port {} failed",
                                serviceMeta.getServiceName(), serviceMeta.getServicePort());
                        eventLoopGroup.shutdownGracefully();
                    }
                }
            });
            //5.向远程服务发送请求
            future.channel().writeAndFlush(rpcProtocol);
        }
    }
}
