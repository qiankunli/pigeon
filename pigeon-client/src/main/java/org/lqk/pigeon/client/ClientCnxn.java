package org.lqk.pigeon.client;

import com.google.common.util.concurrent.SettableFuture;
import org.lqk.pigeon.codec.ClientRecordSerializer;
import org.lqk.pigeon.common.proto.Packet;
import org.lqk.pigeon.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by bert on 2017/3/19
 * <p>
 * record request ==> record response
 */
public class ClientCnxn {

    private ClientCnxnSocket clientCnxnSocket;

    private Executor executor;

    private static Logger log = LoggerFactory.getLogger(ClientCnxn.class);


    public ClientCnxn(String ip, int port, ClientRecordSerializer clientRecordSerializer) {
        clientCnxnSocket = new ClientCnxnSocketNetty(ip, port, clientRecordSerializer);
        executor = Executors.newFixedThreadPool(2);
    }

    public void start() throws IOException, InterruptedException {
        clientCnxnSocket.connect();
    }

    Future<Record> submitRequest(RequestHeader requestHeader, Record request) throws InterruptedException, IOException {
        log.debug("is connected {}", clientCnxnSocket.isConnected());
        ReplyHeader r = new ReplyHeader();
        final Packet packet = new Packet(requestHeader, r, request, null, null);
        final SettableFuture<Packet> packetSettableFuture = clientCnxnSocket.sendPacket(packet);
        final SettableFuture<Record> recordSettableFuture = SettableFuture.create();
        packetSettableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                recordSettableFuture.set(packet.getResponse());
            }
        }, executor);
        return recordSettableFuture;
    }

    public void close() {
        clientCnxnSocket.close();
    }


}
