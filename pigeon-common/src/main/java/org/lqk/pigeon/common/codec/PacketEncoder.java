package org.lqk.pigeon.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import org.lqk.pigeon.Constant;
import org.lqk.pigeon.codec.RecordEncoder;
import org.lqk.pigeon.exception.PigeonException;
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

            int len = buf.readableBytes();

            if (len > Constant.MAX_PACKET_SIZE) {
                throw new EncoderException("packet size is bigger than " + Constant.MAX_PACKET_SIZE);
            }

            log.debug("hex dump {}", ByteBufUtil.hexDump(buf));

            /*
                将buf中的数据写入out中 frameSize + frameData
             */
            out.writeInt(len).writeBytes(buf);
        } catch (Throwable e) {
            throw new EncoderException("encode segment message failed", e);
        } finally {
            ReferenceCountUtil.release(buf);
        }
    }

    abstract void encode(Packet packet, ByteBuf out) throws PigeonException;


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.warn("encode segment message error,close connection", cause);
        /*
            当发现解析异常时,直接关闭连接
         */
        ctx.close();
    }
}
