## 简介

模仿zookeeper,基于netty做一个简单的数据通信的封装。以学习netty在分层框架中的使用方式

注意,这个和一个rpc的服务治理框架是不同的

计划

1. 基本的packet发送与接收（已完成）
2. 支持callback
3. 支持future
4. 支持心跳包,并以此更新客户端的状态
5. 配置数据规范化，即一个框架的运行需要大量配置，比如一个server socket的backlog、一次传输支持的最大byte size等
6. 服务端和客户端连接池的管理（服务端肯定有多个连接，而客户端，可不是想当然的只有一个连接的）



## 使用

定义请求和返回数据

    Packet {
        RequestHeader requestHeader;        // 请求header，可以自定义实现该接口
        ReplyHeader replyHeader;            // 响应header，目前只定义了err，可以自定义实现其他属性
        Record request;                     // 请求body，可以自定义实现
        Record response;                    // 响应body，可以自定义实现
    }
    
定义请求和返回数据的encoder和decoder，以实现ClientRecordSerializer和ServerRecordSerializer

### client

    参见pigeon-client test 类中TestClient

    Packet submit(RequestHeader requestHeader, Record request)
    
### server

    参见pigeon-server test类中的TestServer
    
    自定义实现PacketHandler，处理消息
    
    
### server


## 通用的数据传输碰到的问题

如果是要支持一个通用的数据传送，一个比较大的问题是，你无法预设协议，那么底层传送Packet的协议就只能是

`|id|requestHeader|requestBody|` 和 `|id|replyHeader|responseBody|`

requestBody 和 responseBody 就只能是二进制了。

也就是说，要么传入序列化信息，要么弄一个统一的方式，否则Packet无法自己完成序列化。



||任务|数据model|
|---|---|---|
|上层|||
|业务层||record|
|transport层||packet|


## zk为什么只是读取数据流，然后自己解析model

因为zk抽象的接口是

    `ReplyHeader submitRequest(RequestHeader h, Record request,
                Record response, WatchRegistration watchRegistration)`
                
                
Response 是已经创建好传入的，而netty的一整套model编解码方案，导致
       返回的model已经在框架内创建好，按照java的语法，是无法回传的用户的的response的
       除非事先定义好转换接口。
       
       
## 状态管理

如果一个client只有一个channel，那么client的状态和channel的状态是基本一致的，心跳也可以根据channel的状态来判断是否重连

如果一个client有多个channel，那么client本身也要维护一个状态，心跳只能影响某个连接