package org.lqk.pigeon.common.demo;

import io.netty.buffer.ByteBuf;
import org.lqk.pigeon.codec.RecordEncoder;

import java.nio.charset.Charset;

/**
 * Created by bert on 2017/3/24.
 */
public class StringRecordEncoder implements RecordEncoder<StringRecord> {
    @Override
    public void encode(StringRecord record, ByteBuf out) {
        byte[] b = record.getData().getBytes(Charset.forName("UTF-8"));
        out.writeInt(b.length);
        out.writeBytes(b);
    }
}
