package org.lqk.pigeon.common.concurrent;

import com.google.common.util.concurrent.SettableFuture;
import org.lqk.pigeon.common.proto.Packet;

/**
 * SettableFuture with a created Time
 *
 * @author caorong
 */
public class EffectiveSettableFuture {

    private long createdTs;
    private SettableFuture<Packet> settableFuture;

    public EffectiveSettableFuture(long createdTs) {
        this.createdTs = createdTs;
        this.settableFuture = SettableFuture.create();
    }

    public long getCreatedTs() {
        return createdTs;
    }

    public SettableFuture<Packet> getSettableFuture() {
        return settableFuture;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EffectiveSettableFuture{");
        sb.append("createdTs=").append(createdTs);
        sb.append(", settableFuture=").append(settableFuture);
        sb.append('}');
        return sb.toString();
    }
}
