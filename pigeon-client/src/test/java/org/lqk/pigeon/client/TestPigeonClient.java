package org.lqk.pigeon.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lqk.pigeon.codec.RecordSerializer;
import org.lqk.pigeon.common.demo.DemoRequest;
import org.lqk.pigeon.common.demo.DemoResponse;
import org.lqk.pigeon.common.demo.StringRecordDecoder;
import org.lqk.pigeon.common.demo.StringRecordEncoder;

import java.io.IOException;

/**
 * Created by bert on 2017/3/24.
 */
public class TestPigeonClient {
    PigeonClient pigeonClient = null;

    @Before
    public void before() throws IOException, InterruptedException {
        pigeonClient = new PigeonClient("127.0.0.1", 8080, new RecordSerializer(new StringRecordEncoder(), new StringRecordDecoder()));
        pigeonClient.start();
    }

    @After
    public void close() {
        pigeonClient.close();
    }

    @Test
    public void test() throws Exception {
        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setData("hello");
        DemoResponse demoResponse = new DemoResponse();
        pigeonClient.transport(demoRequest, demoResponse);
        System.out.println(demoResponse.getData());
    }
}
