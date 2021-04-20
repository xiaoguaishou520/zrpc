package com.cw.rpc.provider;

import com.cw.rpc.annotation.RpcService;
import com.cw.rpc.cache.LocalCache;
import com.cw.rpc.codec.RpcDecoder;
import com.cw.rpc.codec.RpcEncoder;
import com.cw.rpc.core.RegistryService;
import com.cw.rpc.core.RpcUtils;
import com.cw.rpc.core.ServiceMeta;
import com.cw.rpc.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author 小怪兽
 * @Date 2021-04-01
 * 启动本地服务，将服务发布到注册中心
 * InitializingBean 配置文件注入后操作的接口
 * BeanPostProcessor Bean初始化前后操作的接口
 */
@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private RegistryService registryService;

    private int serverPort;

    private String serverAddr;


    public RpcProvider(RegistryService registryService, int servicePort) {
        this.registryService = registryService;
        this.serverPort = servicePort;
    }

    /**
     * 当属性注入完毕，就可以触发方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //内部单独启动一个线程来发布服务
        new Thread(()->{
            try {
                startRpcServer();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 启动服务，并开启端口监听客户端的请求
     */
    private void startRpcServer() throws UnknownHostException{
        //1.获取当前服务器的地址
        this.serverAddr = InetAddress.getLocalHost().getHostAddress();

        //2.采用Netty基于tcp协议启动服务
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加自定义编解码器
                        pipeline.addLast(new RpcEncoder());
                        pipeline.addLast(new RpcDecoder());
                        //添加自定义处理器，处理客户端的请求
                        pipeline.addLast(new RpcRequestHandler());
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE,true);

        try {
            ChannelFuture future = serverBootstrap.bind(this.serverAddr, this.serverPort);
            log.info("sever start on {}:{}",this.serverAddr,this.serverPort);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 在初始化bean初始化之后触发
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //1.扫描包含@RpcService注解的类
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        //2.如果包含，构建服务元信息，保存到注册中心
        if (rpcService != null) {
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setServiceAddr(serverAddr);
            serviceMeta.setServicePort(serverPort);
            serviceMeta.setServiceName(rpcService.serviceInterface().getName());
            serviceMeta.setVersion(rpcService.version());

            //注册到注册中心
            try {
                registryService.register(serviceMeta);
                //3.本地缓存保存一份
                LocalCache.rpcServiceMap.put(
                        RpcUtils.buildSeviceKey(serviceMeta.getServiceName(),serviceMeta.getVersion()),bean);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("register service {} failed!",serviceMeta.getServiceName());
            }
        }
        return bean;
    }
}
