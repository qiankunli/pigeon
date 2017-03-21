package org.lqk.pigeon.client;

import org.lqk.pigeon.common.PigeonException;
import org.lqk.pigeon.common.proto.Record;
import org.lqk.pigeon.common.proto.ReplyHeader;
import org.lqk.pigeon.common.proto.RequestHeader;


/**
 * Created by bert on 2017/3/19.
 */
public class PigeonClient {
    private ClientCnxn clientCnxn;

    public Record send(Record request) throws InterruptedException, PigeonException {


        RequestHeader requestHeader = new RequestHeader();
        Record response = null;

        ReplyHeader r = clientCnxn.submitRequest(requestHeader, request, response);

        if (r.getErr() != 0) {
            throw new PigeonException(r.getErr());
        }

        return response;
    }
}
