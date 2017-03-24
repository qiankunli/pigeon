package org.lqk.pigeon.codec;

import org.lqk.pigeon.proto.Record;

/**
 * Created by bert on 2017/3/24.
 */
public class ClientRecordSerializer<T extends Record> extends RecordSerializer {
    public ClientRecordSerializer(RecordEncoder requestEncoder, RecordDecoder responseDecoder) {
        super(requestEncoder, responseDecoder);
    }
}
