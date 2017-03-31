package org.lqk.pigeon.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.lqk.pigeon.Constant;
import org.lqk.pigeon.codec.ServerRecordSerializer;
import org.lqk.pigeon.server.handler.ServerHeartBeatHandler;
import org.lqk.pigeon.exception.PigeonException;
import org.lqk.pigeon.common.codec.*;
import org.lqk.pigeon.common.proto.Packet;
import org.lqk.pigeon.common.proto.PacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


/**
 * Created by bert on 2017/3/22.
 */
public class PigeonServer {

    private int port;

    private PacketHandler packetHandler;

    private ServerBootstrap bootstrap;
    private Channel serverChannel;
    private EventLoopGroup acceptorGroup;
    private EventLoopGroup ioGroup;

    private ServerRecordSerializer serverRecordSerializer;

    private static Logger log = LoggerFactory.getLogger(PigeonServer.class);


    public PigeonServer(int port, PacketHandler packetHandler, ServerRecordSerializer serverRecordSerializer) {
        this.port = port;
        this.packetHandler = packetHandler;
        this.serverRecordSerializer = serverRecordSerializer;
        acceptorGroup = new NioEventLoopGroup(1);
        ioGroup = new NioEventLoopGroup(2);
    }

    public void open() throws PigeonException {
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline channelPipeline = ch.pipeline();
                channelPipeline.addLast(new ResponsePacketEncoder(serverRecordSerializer.getRecordEncoder()))
                        /*
                            客户端至少隔5s发一次消息，那么服务端极端情况下，至少10会收一次消息
                         */
                        .addLast(new IdleStateHandler(10, 0, 0))
                        /*
                            LengthFieldBasedFrameDecoder会去掉长度部分，将流拆成 packet 或 心跳包
                         */
                        .addLast(new LengthFieldBasedFrameDecoder(Constant.MAX_PACKET_SIZE,0,4,0,4))
                        /*
                            心跳包必须在协议解析之前
                         */
                        .addLast(new ServerHeartBeatHandler())
                        .addLast(new RequestPacketDecoder(serverRecordSerializer.getRecordDecoder()))
                        .addLast(new NettyServerChannelHandler(packetHandler));
            }
        };

        bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.group(acceptorGroup, ioGroup).channel(NioServerSocketChannel.class).childHandler(channelInitializer)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true);

        ChannelFuture bindFuture = null;
        try {
            bindFuture = bootstrap.bind(new InetSocketAddress(port)).sync();
        } catch (InterruptedException e) {
            throw new PigeonException("open netty server failed.", e);
        }
        if (!bindFuture.isSuccess()) {
            throw new PigeonException("bind server with port:" + port + " failed.");
        }
        serverChannel = bindFuture.channel();

    }


    private class NettyServerChannelHandler extends SimpleChannelInboundHandler<Packet> {
        private PacketHandler packetHandler;

        public NettyServerChannelHandler(PacketHandler packetHandler) {
            this.packetHandler = packetHandler;
        }

        protected void channelRead0(final ChannelHandlerContext ctx, final Packet packet) throws Exception {
            ctx.executor().execute(new Runnable() {
                public void run() {
                    Packet re = packetHandler.handle(packet);
                    log.debug("handle packet complete,write it ,id {}", packet.getId());
                    ctx.writeAndFlush(re).addListener(new GenericFutureListener<Future<? super Void>>() {
                        @Override
                        public void operationComplete(Future<? super Void> future) throws Exception {
                            if (!future.isSuccess()) {
                                Throwable cause = future.cause();
                                log.error(cause.getMessage(), cause);
                            }
                        }
                    });
                }
            });
        }
    }

    public void close() {
        try {
            if (null != acceptorGroup) {
                acceptorGroup.shutdownGracefully();
            }
            if (null != ioGroup) {
                ioGroup.shutdownGracefully();
            }
            if (null != serverChannel) {
                serverChannel.close().syncUninterruptibly();
            }
            log.info("netty server close success.");
        } catch (Exception e) {
            log.error("netty server close error.", e);
        }
    }
}
