package org.lqk.pigeon.common.proto;

/**
 * Created by bert on 2017/3/22.
 */
public enum PacketType {
    /*
        client send
        client receive
        server send
        server receive
     */

    cs(0), cr(1), ss(2), sr(3), ping(4), pong(5);

    private int value;

    private PacketType(int value) {
        this.value = value;
    }

    public static PacketType valueOf(int value) {
        switch (value) {
            case 0:
                return cs;
            case 1:
                return cr;
            case 2:
                return ss;
            case 3:
                return sr;
            case 4:
                return ping;
            case 5:
                return pong;
            default:
                return null;
        }
    }

    public int getValue() {
        return value;
    }
}
