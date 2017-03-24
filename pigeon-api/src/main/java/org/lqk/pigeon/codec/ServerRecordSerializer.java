package org.lqk.pigeon.codec;

import org.lqk.pigeon.proto.Record;

/**
 * Created by bert on 2017/3/24.
 */
public class ServerRecordSerializer<T extends Record> extends RecordSerializer {
    public ServerRecordSerializer(RecordEncoder responseEncoder, RecordDecoder requestDecoder) {
        super(responseEncoder, requestDecoder);
    }
}
