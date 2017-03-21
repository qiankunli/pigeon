package org.lqk.pigeon.common.proto;

import io.netty.buffer.ByteBuf;
import org.lqk.pigeon.common.utils.SystemUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bert on 2017/3/19.
 */
public class Packet implements Record{
    private int id;
    private RequestHeader requestHeader;
    private ReplyHeader replyHeader;
    private Record request;
    private Record response;
    private boolean finished;
    private AsyncCallback callback;
    private static final AtomicInteger ID = new AtomicInteger(0);

    public Packet() {
        this.id =  ID.incrementAndGet();
    }

    public Packet(RequestHeader requestHeader, ReplyHeader replyHeader, Record request, Record response, AsyncCallback callback) {
        super();
        this.requestHeader = requestHeader;
        this.replyHeader = replyHeader;
        this.request = request;
        this.response = response;
        this.callback = callback;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }

    public ReplyHeader getReplyHeader() {
        return replyHeader;
    }

    public void setReplyHeader(ReplyHeader replyHeader) {
        this.replyHeader = replyHeader;
    }

    public Record getRequest() {
        return request;
    }

    public void setRequest(Record request) {
        this.request = request;
    }

    public Record getResponse() {
        return response;
    }

    public void setResponse(Record response) {
        this.response = response;
    }

    public boolean getIsFinished() {
        return finished;
    }

    public void setIsFinished(boolean finished) {
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public AsyncCallback getCallback() {
        return callback;
    }

    public void setCallback(AsyncCallback callback) {
        this.callback = callback;
    }

    @Override
    public void encode(ByteBuf out) {

    }

    @Override
    public void decode(ByteBuf in) {

    }
}
