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
  
1）自定义协议包含MessageHeader和body，其中协议头包含魔数，消息类型，消息状态，序列化类型，消息id，版本号，数据长度等，协议体描述的是请求体或者响应体  
2）在自定义协议对象传输时，需要将协议头跟协议体信息写入到ByteBuf，此时需要先将协议体经过序列化转换为字节数组才能写入ByteBuf  
  
编码器：实现将协议包对象转换为字节流写入ByteBuf  
解码器：实现从ByteBuf中读取字节流信息并转换为协议包对象  
![image](https://github.com/xiaoguaishou520/zrpc/blob/master/images/zrpc-protocol.png)
