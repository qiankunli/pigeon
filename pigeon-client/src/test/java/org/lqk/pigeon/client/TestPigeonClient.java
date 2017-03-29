package org.lqk.pigeon.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lqk.pigeon.codec.ClientRecordSerializer;
import org.lqk.pigeon.common.demo.*;
import org.lqk.pigeon.proto.RequestHeader;

import java.io.IOException;

/**
 * Created by bert on 2017/3/24.
 */
public class TestPigeonClient {
    PigeonClient pigeonClient = null;

    @Before
    public void before() throws IOException, InterruptedException {
        pigeonClient = new PigeonClient("127.0.0.1", 8080, new ClientRecordSerializer(new StringRecordEncoder(),
                new StringRecordDecoder()));
        pigeonClient.start();
    }

    @After
    public void close() {
        pigeonClient.close();
    }

    @Test
    public void testSubmit() throws Exception {
        StringRecord request = new StringRecord();
        request.setData("hello");

        RequestHeader requestHeader = new RequestHeader();
        StringRecord response = (StringRecord)pigeonClient.send(requestHeader, request);
        System.out.println(response.getData());
    }
    @Test
    public void testPing() throws InterruptedException {
        while(true){
            Thread.sleep(1000);
        }
    }
}
