package org.lqk.pigeon.common.demo;

import io.netty.buffer.ByteBuf;
import org.lqk.pigeon.codec.RecordDecoder;

import java.nio.charset.Charset;

/**
 * Created by bert on 2017/3/24.
 */
public class StringRecordDecoder implements RecordDecoder<StringRecord> {
    @Override
    public StringRecord decode(ByteBuf in) {
        StringRecord record = new StringRecord();
        int len = in.readInt();
        byte[] b = new byte[len];
        in.readBytes(b);
        String data = new String(b, Charset.forName("UTF-8"));
        record.setData(data);
        return record;
    }
}
