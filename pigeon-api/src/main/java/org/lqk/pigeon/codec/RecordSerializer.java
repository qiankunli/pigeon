package org.lqk.pigeon.codec;

import org.lqk.pigeon.proto.Record;

/**
 * Created by bert on 2017/3/24.
 */
public class RecordSerializer<T extends Record> {
    protected RecordEncoder<T> recordEncoder;
    protected RecordDecoder<T> recordDecoder;

    public RecordSerializer(RecordEncoder<T> recordEncoder, RecordDecoder<T> recordDecoder) {
        this.recordEncoder = recordEncoder;
        this.recordDecoder = recordDecoder;
    }

    public RecordEncoder<T> getRecordEncoder() {
        return recordEncoder;
    }

    public void setRecordEncoder(RecordEncoder<T> recordEncoder) {
        this.recordEncoder = recordEncoder;
    }

    public RecordDecoder<T> getRecordDecoder() {
        return recordDecoder;
    }

    public void setRecordDecoder(RecordDecoder<T> recordDecoder) {
        this.recordDecoder = recordDecoder;
    }


}
