package org.lqk.pigeon.client;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.lqk.pigeon.Constant;
import org.lqk.pigeon.codec.ClientRecordSerializer;
import org.lqk.pigeon.common.codec.RequestPacketEncoder;
import org.lqk.pigeon.common.codec.ResponsePacketDecoder;
import org.lqk.pigeon.client.handler.ClientHeartBeatHandler;
import org.lqk.pigeon.common.concurrent.EffectiveSettableFuture;
import org.lqk.pigeon.common.proto.Packet;
import org.lqk.pigeon.proto.ReplyHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by bert on 2017/3/19.
 */
public class ClientCnxnSocketNetty extends ClientCnxnSocket {

    //todo 是否要有状态字段,为什么必须有


    Bootstrap bootstrap = new Bootstrap();
    EventLoopGroup eventLoopGroup = null;
    Channel channel = null;

    // todo 加一个逻辑，周期性移除callbackMap中已经无效的数据

    /*
        因为packet的返回值是异步返回的，因此，要么packet保存isFinished的字段，然后上层轮询
        要么通过future封装下一下返回值
     */
    protected ConcurrentMap<Integer, EffectiveSettableFuture> callbackMap = new ConcurrentHashMap<Integer, EffectiveSettableFuture>(
            1000);

    private static Logger log = LoggerFactory.getLogger(ClientCnxnSocketNetty.class);

    public ClientCnxnSocketNetty(String ip, int port, ClientRecordSerializer clientRecordSerializer) {
        super(ip, port, clientRecordSerializer);
    }


    @Override
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    @Override
    public void connect() throws IOException, InterruptedException {

        eventLoopGroup = new NioEventLoopGroup(2);

        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ClientChannelInitializer());

        eventLoopGroup.scheduleAtFixedRate(new TimeoutMonitorRunnable(), 30, 30, TimeUnit.SECONDS);


        bootstrap.connect(new InetSocketAddress(ip, port)).sync().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                channel = future.channel();
            }
        });


    }

    @Override
    public SettableFuture<Packet> sendPacket(final Packet p) throws IOException {
        final EffectiveSettableFuture effectiveSettableFuture = new EffectiveSettableFuture(System.currentTimeMillis());
        callbackMap.put(p.getId(), effectiveSettableFuture);
        channel.writeAndFlush(p).addListener(new GenericFutureListener() {

            @Override
            public void operationComplete(Future future) throws Exception {
                if (!future.isSuccess()) {
                    ReplyHeader r = new ReplyHeader();
                    r.setErr(-1);
                    effectiveSettableFuture.getSettableFuture().setException(future.cause());
                    p.setReplyHeader(r);
                }
            }
        });
        return effectiveSettableFuture.getSettableFuture();
    }

    @Override
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }


    /**
     * 回收超时的Future 30秒
     */
    class TimeoutMonitorRunnable implements Runnable {

        private long timeoutThreshold;

        public TimeoutMonitorRunnable() {
            timeoutThreshold = 1000 * 30;
        }

        @Override
        public void run() {
            try {
                long startTs = System.currentTimeMillis();
                int size = callbackMap.size();
                log.debug("recycle timeout future, size is {}", size);
                for (Map.Entry<Integer, EffectiveSettableFuture> entry : callbackMap.entrySet()) {
                    try {
                        if (startTs - entry.getValue().getCreatedTs() > timeoutThreshold) {
                            callbackMap.remove(entry.getKey());
                            // TODO 改成setException 更好?
                            entry.getValue().getSettableFuture().cancel(true);
                        }
                    } catch (Exception e) {
                        log.error("clear timeout future error!", e);
                    }
                }
                long stopTs = System.currentTimeMillis();
                long use = stopTs - startTs;
                log.debug("recycle {} timeout future total use {}ms", size, use);
            } catch (Exception e) {
                log.error("recycle timeout future error!", e);
            }
        }
    }

    private class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel arg0) throws Exception {
            arg0.pipeline().addLast(new RequestPacketEncoder(clientRecordSerializer.getRecordEncoder()))
                    /*
                            客户端读写idle超过5秒发送ping请求
                         */
                    .addLast(new IdleStateHandler(0, 0, 5))
                    /*
                        LengthFieldBasedFrameDecoder会去掉长度部分，剩下的数据部分buf交给PacketDecoder处理
                     */
                    .addLast(new LengthFieldBasedFrameDecoder(Constant.MAX_PACKET_SIZE, 0, 4, 0, 4))
                    .addLast(new ClientHeartBeatHandler(ClientCnxnSocketNetty.this))
                    .addLast(new ResponsePacketDecoder(clientRecordSerializer.getRecordDecoder()))
                    .addLast(new PacketHandler());
        }
    }

    private class PacketHandler extends SimpleChannelInboundHandler<Packet> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
            EffectiveSettableFuture effectiveSettableFuture = callbackMap.get(packet.getId());
            effectiveSettableFuture.getSettableFuture().set(packet);
        }
    }
}
