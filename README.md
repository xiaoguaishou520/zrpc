# zrpc 自定义RPC框架（基于Zookeeper+Netty）
包含六大模块：zrpc-core（基础架构模块），zrpc-protocol（自定义协议模块），zrpc-registry（注册中心模块），zrpc-interface（通用接口模块），zrpc-provider（生产者模块），zrpc-consumer（消费者模块）

## 1，zrpc-core模块
**【功能】**  
**编写一些相关的基础类，供给其他模块使用**  
  
请求类：RpcRequest  
响应类：RpcResponse  
服务元数据类：ServiceMeta  
请求异步结果类：RpcFuture  
工具类：RpcUtils  
![image](https://github.com/xiaoguaishou520/zrpc/blob/master/images/zrpc-core.png)

## 2，zrpc-protocol模块
**【功能】**  
**实现自定义协议设计+编解码+序列化**  
  
**【实现逻辑】**  
1）自定义协议包含MessageHeader和body，其中协议头包含魔数，消息类型，消息状态，序列化类型，消息id，版本号，数据长度等，协议体描述的是请求体或者响应体  
2）在自定义协议对象传输时，需要将协议头跟协议体信息写入到ByteBuf，此时需要先将协议体经过序列化转换为字节数组才能写入ByteBuf  
3）编写编码器与解码器，实现Netty进行对象的传输  
  
编码器：实现将协议包对象转换为字节流写入ByteBuf  
解码器：实现从ByteBuf中读取字节流信息并转换为协议包对象  
![image](https://github.com/xiaoguaishou520/zrpc/blob/master/images/zrpc-protocol.png)  

## 3，zrpc-registry模块
**【功能】**  
**实现服务的注册与发现**  
  
**【实现逻辑】**  
1）该模块使用的是zookeeper注册中心，利用curator框架实现服务的注册与发现，便于开发  
2）负载均衡算法为一致性hash算法：创建一个tree，每个节点多设置一个虚拟节点，客户端根据自身定义的hashcode在树上去寻找最近的服务端节点  

![image](https://github.com/xiaoguaishou520/zrpc/blob/master/images/zrpc-registry.png)
  
## 4，zrpc-provider模块
**【功能】**  
**实现服务的提供**  
  
**【实现逻辑】**  
1）编写带有注册中心配置的配置文件application.properties，在服务启动的时候读取配置文件的信息  
2）在bean初始化之后扫描带有@RpcService注解的类，构建服务元信息保存到注册中心并本地缓存一份  
3）配置文件注入完毕之后，开启一个Netty服务端，发布服务监听客户端的请求  
4）编写服务端业务处理器，处理客户端的业务请求（解析请求协议包，反馈响应协议包）  
  
![image](https://github.com/xiaoguaishou520/zrpc/blob/master/images/zrpc-provider.png)

## 5,zrpc-consumer
**【功能】**  
**调用远程服务**  
  
**【实现逻辑】**  
1）在bean初始化之前扫描带有@RpcReference注解的bean，并将之实例化，并注册到Spring容器  
2）生成代理对象，开启一个Netty客户端，在注册中心通过一致性hash算法寻找对应的服务，向服务发起请求获取结果  
3）编写客户端netty业务逻辑的处理器，处理异步结果  
  
![image](https://github.com/xiaoguaishou520/zrpc/blob/master/images/rpc-consumer.png)

