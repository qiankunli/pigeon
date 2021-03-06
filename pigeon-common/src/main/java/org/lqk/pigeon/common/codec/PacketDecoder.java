package org.lqk.pigeon.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import org.lqk.pigeon.codec.RecordDecoder;
import org.lqk.pigeon.common.proto.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by bert on 16/9/7.
 */
public abstract class PacketDecoder extends ByteToMessageDecoder {

    protected RecordDecoder recordDecoder;

    public PacketDecoder(RecordDecoder recordDecoder) {
        this.recordDecoder = recordDecoder;
    }

    private static Logger log = LoggerFactory.getLogger(PacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            log.debug("hex dump {}", ByteBufUtil.hexDump(in));
            Packet packet = new Packet();
            decode(packet, in);
            out.add(packet);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new DecoderException("segment decoder decode failed.", e);
        }
    }


    abstract void decode(Packet packet, ByteBuf in);


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.warn("decode segment message error,close connection", cause);
        /*
            当发现解析异常时,直接关闭连接
         */
        ctx.close();
    }
}
