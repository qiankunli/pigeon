package org.lqk.pigeon.common.proto;

import io.netty.buffer.ByteBuf;


/**
 * Created by bert on 2017/3/19.
 */
public interface Record {
    void encode(ByteBuf out);
    void decode(ByteBuf in);
}
