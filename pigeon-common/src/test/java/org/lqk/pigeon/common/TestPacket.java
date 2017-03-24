package org.lqk.pigeon.common;

import org.junit.Test;
import org.lqk.pigeon.proto.Packet;

/**
 * Created by bert on 2017/3/19.
 */
public class TestPacket {
    @Test
    public void test(){
        Packet packet = new Packet();
        System.out.println(packet.getId());

        Packet packet2 = new Packet();
        System.out.println(packet2.getId());
    }
}
