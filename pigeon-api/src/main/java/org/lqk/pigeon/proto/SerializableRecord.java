package org.lqk.pigeon.proto;

import io.netty.buffer.ByteBuf;


/**
 * Created by bert on 2017/3/19.
 *
 * 成员已经确定，可以直接序列化
 */
public interface SerializableRecord {
    void encode(ByteBuf out);
    void decode(ByteBuf in);
}
