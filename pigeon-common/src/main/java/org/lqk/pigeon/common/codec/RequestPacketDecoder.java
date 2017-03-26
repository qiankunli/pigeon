package org.lqk.pigeon.common.codec;

import io.netty.buffer.ByteBuf;
import org.lqk.pigeon.codec.RecordDecoder;
import org.lqk.pigeon.common.proto.Packet;

/**
 * Created by bert on 2017/3/24.
 */
public class RequestPacketDecoder extends PacketDecoder {

    public RequestPacketDecoder(RecordDecoder requestDecoder) {
        super(requestDecoder);
    }

    @Override
    void decode(Packet packet, ByteBuf in) {
        packet.decodeRequest(in, recordDecoder);
    }
}
