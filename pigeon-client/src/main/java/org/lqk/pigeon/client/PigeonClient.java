package org.lqk.pigeon.client;

import org.lqk.pigeon.codec.ClientRecordSerializer;
import org.lqk.pigeon.exception.PigeonException;
import org.lqk.pigeon.common.proto.Packet;
import org.lqk.pigeon.proto.Record;
import org.lqk.pigeon.proto.ReplyHeader;
import org.lqk.pigeon.proto.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Created by bert on 2017/3/19.
 */
public class PigeonClient {
    private ClientCnxn clientCnxn;

    private static Logger log = LoggerFactory.getLogger(PigeonClient.class);

    public PigeonClient(String ip, int port, ClientRecordSerializer clientRecordSerializer) {
        clientCnxn = new ClientCnxn(ip, port, clientRecordSerializer);
    }

    public void start() throws IOException, InterruptedException {
        clientCnxn.start();
    }

    public Record submit(RequestHeader requestHeader, Record request) throws Exception {

        Packet packet = clientCnxn.submitRequest(requestHeader, request);
        ReplyHeader r = packet.getReplyHeader();
        if (r.getErr() != 0) {
            throw new PigeonException(r.getErr());
        }

        return packet.getResponse();

    }

    public void close() {
        clientCnxn.close();
    }
}
