package org.lqk.pigeon.common.proto;

import io.netty.buffer.ByteBuf;

/**
 * Created by bert on 2017/3/19.
 */
public class ReplyHeader implements Record{
    private int err;

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public void encode(ByteBuf out) {
        out.writeInt(err);
    }

    public void decode(ByteBuf in) {
        this.err = in.readInt();
    }
}
