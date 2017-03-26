package org.lqk.pigeon.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.lqk.pigeon.client.ClientCnxnSocketNetty;
import org.lqk.pigeon.common.handler.HeartBeatHandler;
import org.lqk.pigeon.common.proto.PacketType;

/**
 * Created by bert on 2017/3/26.
 */
public class ClientHeartBeatHandler extends HeartBeatHandler {

    private ClientCnxnSocketNetty clientCnxnSocketNetty;

    public ClientHeartBeatHandler(ClientCnxnSocketNetty clientCnxnSocketNetty){
        this.clientCnxnSocketNetty = clientCnxnSocketNetty;
    }

    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        super.handleAllIdle(ctx);
        sendPingMsg(ctx);

    }

    private void sendPingMsg(ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer(4);
        buf.writeInt(4);
        buf.writeInt(PacketType.ping.getValue());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        /*
            断掉时重新连接
         */
        clientCnxnSocketNetty.connect();

    }
}
