package org.lqk.pigeon.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lqk.pigeon.codec.ServerRecordSerializer;
import org.lqk.pigeon.common.PigeonException;
import org.lqk.pigeon.common.codec.*;
import org.lqk.pigeon.proto.Packet;
import org.lqk.pigeon.proto.PacketHandler;
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
                channelPipeline.addLast(new RequestPacketEncoder(serverRecordSerializer.getRecordEncoder()))
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
                    ctx.writeAndFlush(re);
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
