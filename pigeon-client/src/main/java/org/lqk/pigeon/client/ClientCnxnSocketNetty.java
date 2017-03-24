package org.lqk.pigeon.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.lqk.pigeon.codec.ClientRecordSerializer;
import org.lqk.pigeon.common.codec.RequestPacketEncoder;
import org.lqk.pigeon.common.codec.ResponsePacketDecoder;
import org.lqk.pigeon.proto.Packet;
import org.lqk.pigeon.proto.ReplyHeader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by bert on 2017/3/19.
 */
public class ClientCnxnSocketNetty extends ClientCnxnSocket {

    //todo 是否要有状态字段,为什么必须有


    Bootstrap bootstrap = new Bootstrap();
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
    Channel channel = null;

    // todo 加一个逻辑，周期性移除callbackMap中已经无效的数据

    /*
        因为packet的返回值是异步返回的，因此，要么packet保存isFinished的字段，然后上层轮询
        要么通过future封装下一下返回值
     */
    final ConcurrentMap<Integer, Packet> callbackMap = new ConcurrentHashMap<Integer, Packet>(
            1000);

    public ClientCnxnSocketNetty(ClientRecordSerializer clientRecordSerializer){
        super(clientRecordSerializer);
    }


    @Override
    boolean isConnected() {
        return channel != null && channel.isActive();
    }

    @Override
    void connect(InetSocketAddress addr) throws IOException, InterruptedException {

        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ClientChannelInitializer());


        bootstrap.connect(addr).sync().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                channel = future.channel();
            }
        });


    }

    @Override
    void sendPacket(final Packet p) throws IOException {
        callbackMap.put(p.getId(), p);
        channel.writeAndFlush(p).addListener(new GenericFutureListener(){

            @Override
            public void operationComplete(Future future) throws Exception {
                if(!future.isSuccess()){
                    ReplyHeader r = new ReplyHeader();
                    r.setErr(-1);
                    p.setIsFinished(true);
                    p.setReplyHeader(r);
                }
            }
        });
    }

    @Override
    void close() {
        eventLoopGroup.shutdownGracefully();

        // todo 是否删除pendingQueue
    }


    private class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel arg0) throws Exception {
            arg0.pipeline().addLast(new RequestPacketEncoder(clientRecordSerializer.getRecordEncoder()))
                    .addLast(new ResponsePacketDecoder(clientRecordSerializer.getRecordDecoder()))
                    .addLast(new PacketHandler());
        }
    }

    private class PacketHandler extends SimpleChannelInboundHandler<Packet> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
            Packet clientPacket = callbackMap.get(packet.getId());
            clientPacket.setReplyHeader(packet.getReplyHeader());
            clientPacket.setResponse(packet.getResponse());
            clientPacket.setIsFinished(true);
        }
    }
}
