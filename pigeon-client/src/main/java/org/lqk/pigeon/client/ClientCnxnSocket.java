package org.lqk.pigeon.client;

import org.lqk.pigeon.codec.RecordSerializer;
import org.lqk.pigeon.proto.Packet;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by bert on 2017/3/19.
 */
public abstract class ClientCnxnSocket {

    protected RecordSerializer recordSerializer;

    public ClientCnxnSocket(RecordSerializer recordSerializer) {
        this.recordSerializer = recordSerializer;
    }

    abstract boolean isConnected();

    abstract void connect(InetSocketAddress addr) throws IOException, InterruptedException;

    abstract void sendPacket(Packet p) throws IOException;

    abstract void close();
}
