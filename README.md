# zrpc 自定义RPC框架（基于Zookeeper+Netty）
包含六大模块：zrpc-core（基础架构模块），zrpc-protocol（自定义协议模块），zrpc-registry（注册中心模块），zrpc-interface（通用接口模块），zrpc-provider（生产者模块），zrpc-consumer（消费者模块）

## 1，zrpc-core模块
【功能】  
编写一些相关的基础类，供给其他模块使用  
  
请求类：RpcRequest  
响应类：RpcResponse  
服务元数据类：ServiceMeta  
请求异步结果类：RpcFuture  
工具类：RpcUtils  
![image](https://github.com/xiaoguaishou520/zrpc/blob/master/images/zrpc-core.png)

## 2，zrpc-protocol模块
![image](https://github.com/xiaoguaishou520/zrpc/blob/master/images/zrpc-protocol.png)
