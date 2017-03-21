package org.lqk.pigeon.client;

import org.lqk.pigeon.common.proto.Packet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by bert on 2017/3/19.
 */
public abstract class ClientCnxnSocket {

    abstract boolean isConnected();

    abstract void connect(InetSocketAddress addr) throws IOException, InterruptedException;

    abstract void sendPacket(Packet p) throws IOException;

    abstract void close();
}
