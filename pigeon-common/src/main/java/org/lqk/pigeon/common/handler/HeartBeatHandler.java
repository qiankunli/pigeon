package org.lqk.pigeon.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.lqk.pigeon.common.proto.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bert on 2017/3/26.
 */
public abstract class HeartBeatHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static Logger log = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        int packetType = buf.readInt();
        if (PacketType.ping.getValue() == packetType) {
            sendPongMsg(ctx);
        } else if (PacketType.pong.getValue() == packetType) {
            log.debug("receive pong message from {}", ctx.channel().remoteAddress());
        } else {
            ctx.fireChannelRead(buf);
        }
    }


    private void sendPongMsg(ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer(4);
        buf.writeInt(4);
        buf.writeInt(PacketType.pong.getValue());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.error("{} is active", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("{} is inactive", ctx.channel().remoteAddress());
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        log.debug("---READER_IDLE---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        log.debug("---WRITER_IDLE---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        log.debug("---ALL_IDLE---");
    }
}
