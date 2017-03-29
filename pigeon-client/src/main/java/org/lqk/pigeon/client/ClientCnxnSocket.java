package org.lqk.pigeon.client;

import com.google.common.util.concurrent.SettableFuture;
import org.lqk.pigeon.codec.ClientRecordSerializer;
import org.lqk.pigeon.common.proto.Packet;

import java.io.IOException;

/**
 * Created by bert on 2017/3/19.
 * packet request ==> packet response
 */
public abstract class ClientCnxnSocket {

    protected ClientRecordSerializer clientRecordSerializer;

    protected String ip;
    protected int port;

    public ClientCnxnSocket(String ip,int port,ClientRecordSerializer clientRecordSerializer) {
        this.clientRecordSerializer = clientRecordSerializer;
        this.ip = ip;
        this.port = port;
    }

    abstract boolean isConnected();

    abstract void connect() throws IOException, InterruptedException;

    abstract SettableFuture<Packet> sendPacket(Packet p) throws IOException;

    abstract void close();
}
