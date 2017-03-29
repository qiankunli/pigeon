package org.lqk.pigeon.client;

import org.lqk.pigeon.codec.ClientRecordSerializer;
import org.lqk.pigeon.proto.Record;
import org.lqk.pigeon.proto.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Future;


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

    public Future<Record> send(RequestHeader requestHeader, Record request) throws Exception {

        return clientCnxn.submitRequest(requestHeader, request);

    }

    public void close() {
        clientCnxn.close();
    }
}
