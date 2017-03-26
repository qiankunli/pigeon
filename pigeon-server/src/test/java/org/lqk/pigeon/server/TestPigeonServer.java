package org.lqk.pigeon.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lqk.pigeon.codec.ServerRecordSerializer;
import org.lqk.pigeon.exception.PigeonException;
import org.lqk.pigeon.common.demo.*;
import org.lqk.pigeon.common.proto.Packet;
import org.lqk.pigeon.common.proto.PacketHandler;
import org.lqk.pigeon.proto.ReplyHeader;


/**
 * Created by bert on 2017/3/22.
 */
public class TestPigeonServer {

    PigeonServer pigeonServer = null;

    @Before
    public void before() throws PigeonException {
        pigeonServer = new PigeonServer(8080, new PacketHandler() {
            public Packet handle(Packet packet) {
                System.out.println("id ==> " + packet.getId());
                StringRecord request =  (StringRecord) packet.getRequest();
                System.out.println("data ==> " + request.getData());
                StringRecord response = new StringRecord();
                response.setData("hello world");
                packet.setResponse(response);
                ReplyHeader replyHeader = new ReplyHeader();
                replyHeader.setErr(0);
                packet.setReplyHeader(replyHeader);
                return packet;
            }
        },new ServerRecordSerializer(new StringRecordEncoder(),new StringRecordDecoder()));
        pigeonServer.open();
    }

    @After
    public void after() {
        pigeonServer.close();
    }

    @Test
    public void testServer() throws Exception {

        while (true) {
            Thread.sleep(1000);
        }
    }


}
