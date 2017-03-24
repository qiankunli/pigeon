package org.lqk.pigeon.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import org.lqk.pigeon.codec.RecordDecoder;
import org.lqk.pigeon.codec.RecordEncoder;
import org.lqk.pigeon.proto.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by bert on 16/9/7.
 */
public abstract class PacketEncoder extends MessageToByteEncoder<Packet> {

    protected RecordEncoder recordEncoder;

    public PacketEncoder(RecordEncoder recordEncoder) {
        this.recordEncoder = recordEncoder;
    }

    private static Logger log = LoggerFactory.getLogger(PacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        ByteBuf buf = ctx.alloc().directBuffer();
        try {
            /*
                先将数据写入到buf中
             */
            encode(packet, buf);

            /*
                将buf中的数据写入out中 frameSize + frameData
             */
            out.writeInt(buf.readableBytes()).writeBytes(buf);
        } catch (Throwable e) {
            throw new EncoderException("encode segment message failed", e);
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

    abstract void encode(Packet packet, ByteBuf out);
}
