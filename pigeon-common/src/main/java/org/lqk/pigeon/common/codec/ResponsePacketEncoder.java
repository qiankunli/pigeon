package org.lqk.pigeon.common.codec;

import io.netty.buffer.ByteBuf;
import org.lqk.pigeon.codec.RecordEncoder;
import org.lqk.pigeon.exception.PigeonException;
import org.lqk.pigeon.common.proto.Packet;

/**
 * Created by bert on 2017/3/24.
 */
public class ResponsePacketEncoder extends PacketEncoder {

    public ResponsePacketEncoder(RecordEncoder responseEncoder) {
        super(responseEncoder);
    }

    @Override
    void encode(Packet packet, ByteBuf out) throws PigeonException {
        packet.encodeResponse(out, recordEncoder);
    }
}
