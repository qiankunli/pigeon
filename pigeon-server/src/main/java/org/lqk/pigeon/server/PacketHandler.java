package org.lqk.pigeon.server;

import org.lqk.pigeon.proto.Packet;

/**
 * Created by bert on 2017/3/22.
 */
public interface PacketHandler {
    Packet handle(Packet packet);
}
