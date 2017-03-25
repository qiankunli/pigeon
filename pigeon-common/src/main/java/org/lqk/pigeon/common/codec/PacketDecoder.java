package org.lqk.pigeon.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import org.lqk.pigeon.Constant;
import org.lqk.pigeon.codec.RecordDecoder;
import org.lqk.pigeon.proto.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 此处没有用LengthFieldBasedFrameDecoder的原因是,先根据
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
        int available = in.readableBytes();
        if (available <= 4) {
            /*
                本批数据连解析长度都不够(或者只够解析长度),直接返回,下次再来
             */
            return;
        }
        in.markReaderIndex();
        int frameSize = in.readInt();
        if (frameSize >= Constant.MAX_PACKET_SIZE) {
            log.error("frame size {} is bigger than 4MB.", frameSize);
            throw new DecoderException("frame size is bigger than 4MB.");
        }
        /*
            完整数据格式是: frameSize(4 byte) + frameData

            messageSize = frameSize + 4
         */
        if (available < frameSize + 4) {
            /*
                本批数据不够frameSize中指定的长度,直接返回,下次再来
                同时将读指针恢复过来
             */
            in.resetReaderIndex();
            return;
        }
        /*
            将一个完整的数据读取到buf中
         */
        ByteBuf buf = ctx.alloc().heapBuffer();
        buf.writeBytes(in, frameSize);
        try {
            log.debug("hex dump {}", ByteBufUtil.hexDump(buf));
            Packet packet = new Packet();
            decode(packet, buf);
            out.add(packet);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new DecoderException("segment decoder decode failed.", e);
        } finally {
            buf.release();
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
