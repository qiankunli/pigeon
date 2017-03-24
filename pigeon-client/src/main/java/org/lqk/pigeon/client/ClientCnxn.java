package org.lqk.pigeon.client;

import org.lqk.pigeon.codec.RecordSerializer;
import org.lqk.pigeon.proto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by bert on 2017/3/19.
 */
public class ClientCnxn {

    private ClientCnxnSocket clientCnxnSocket;

    private String ip;

    private int port;

    private RecordSerializer recordSerializer;


    private static Logger log = LoggerFactory.getLogger(ClientCnxn.class);


    public ClientCnxn(String ip, int port, RecordSerializer recordSerializer) {
        this.ip = ip;
        this.port = port;
        clientCnxnSocket = new ClientCnxnSocketNetty(recordSerializer);
    }

    public void start() throws IOException, InterruptedException {
        clientCnxnSocket.connect(new InetSocketAddress(ip, port));
    }

    ReplyHeader submitRequest(RequestHeader requestHeader, Record request, Record response) throws InterruptedException, IOException {
        log.debug("is connected {}", clientCnxnSocket.isConnected());
        ReplyHeader r = new ReplyHeader();
        Packet packet = new Packet(requestHeader, r, request, response, null);
        clientCnxnSocket.sendPacket(packet);
        synchronized (packet) {
            while (!packet.getIsFinished()) {
                packet.wait();
            }
        }
        return r;

    }

    public void close() {
        clientCnxnSocket.close();
    }


}
