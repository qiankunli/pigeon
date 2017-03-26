package org.lqk.pigeon.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.lqk.pigeon.common.handler.HeartBeatHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bert on 2017/3/26.
 */
public class ServerHeartBeatHandler extends HeartBeatHandler {
    private static Logger log = LoggerFactory.getLogger(ServerHeartBeatHandler.class);

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        log.error("client {}  read timeout, close it", ctx.channel().remoteAddress());
        ctx.close();
    }


}
