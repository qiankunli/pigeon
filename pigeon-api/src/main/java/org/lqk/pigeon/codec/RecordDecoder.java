package org.lqk.pigeon.codec;

import io.netty.buffer.ByteBuf;
import org.lqk.pigeon.proto.Record;

/**
 * Created by bert on 2017/3/24.
 */
public interface RecordDecoder<T extends Record> {
    T decode(ByteBuf in);
}
