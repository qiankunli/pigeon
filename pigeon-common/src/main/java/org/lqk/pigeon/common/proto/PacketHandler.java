package org.lqk.pigeon.common.proto;


/**
 * Created by bert on 2017/3/22.
 */
public interface PacketHandler {
    Packet handle(Packet packet);
}
