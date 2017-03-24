package org.lqk.pigeon.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lqk.pigeon.common.PigeonException;
import org.lqk.pigeon.common.demo.DemoRequest;
import org.lqk.pigeon.common.demo.DemoResponse;
import org.lqk.pigeon.common.demo.StringRecordDecoder;
import org.lqk.pigeon.common.demo.StringRecordEncoder;
import org.lqk.pigeon.proto.Packet;


/**
 * Created by bert on 2017/3/22.
 */
public class TestPigeonServer {

    PigeonServer pigeonServer = null;

    @Before
    public void before() throws PigeonException {
        pigeonServer = new PigeonServer(8080, new PacketHandler() {
            public Packet handle(Packet packet) {
                DemoRequest request = (DemoRequest) packet.getRequest();
                System.out.println(request.getData());
                DemoResponse response = new DemoResponse();
                request.setData("hello world");
                packet.setResponse(response);
                return packet;
            }
        },new StringRecordEncoder(),new StringRecordDecoder());
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
