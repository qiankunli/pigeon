## 简介

模仿zookeeper,基于netty做一个简单的数据通信的封装。以学习netty在分层框架中的使用方式

注意,这个和一个rpc的服务治理框架是不同的

计划

1. 基本的packet发送与接收
2. 支持callback
3. 支持future
4. 支持心跳包,并以此更新客户端的状态