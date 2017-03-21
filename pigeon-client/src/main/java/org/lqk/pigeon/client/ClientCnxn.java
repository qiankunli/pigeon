package org.lqk.pigeon.client;

import org.lqk.pigeon.common.proto.*;
import org.lqk.pigeon.common.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by bert on 2017/3/19.
 */
public class ClientCnxn {



    private static Logger log = LoggerFactory.getLogger(ClientCnxn.class);






    ReplyHeader submitRequest(RequestHeader requestHeader, Record request, Record response) throws InterruptedException {
        ReplyHeader r = new ReplyHeader();
        Packet packet = new Packet(requestHeader, r, request, response, null);
        synchronized (packet) {
            while (!packet.getIsFinished()) {
                packet.wait();
            }
        }
        return r;

    }




}
