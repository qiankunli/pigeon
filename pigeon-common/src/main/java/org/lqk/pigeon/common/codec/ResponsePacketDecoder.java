package org.lqk.pigeon.common.codec;

import io.netty.buffer.ByteBuf;
import org.lqk.pigeon.codec.RecordDecoder;
import org.lqk.pigeon.proto.Packet;

/**
 * Created by bert on 2017/3/24.
 */
public class ResponsePacketDecoder extends PacketDecoder {

    public ResponsePacketDecoder(RecordDecoder responseDecoder) {
        super(responseDecoder);
    }

    @Override
    void decode(Packet packet, ByteBuf in) {
        packet.decodeResponse(in, recordDecoder);
    }
}
