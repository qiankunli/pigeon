## 简介

模仿zookeeper,基于netty做一个简单的数据通信的封装。以学习netty在分层框架中的使用方式

注意,这个和一个rpc的服务治理框架是不同的

计划

1. 基本的packet发送与接收
2. 支持callback
3. 支持future
4. 支持心跳包,并以此更新客户端的状态


## 通用的数据传输碰到的问题

如果是要支持一个通用的数据传送，一个比较大的问题是，你无法预设协议，那么底层传送Packet的协议就只能是

`|id|requestHeader|requestBody|` 和 `|id|replyHeader|responseBody|`

requestBody 和 responseBody 就只能是二进制了。

也就是说，要么传入序列化信息，要么弄一个统一的方式，否则Packet无法自己完成序列化。