package org.lqk.pigeon.proto;

import io.netty.buffer.ByteBuf;
import org.lqk.pigeon.codec.RecordDecoder;
import org.lqk.pigeon.codec.RecordEncoder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bert on 2017/3/19.
 * <p>
 * id,packetType 是为了确保连接可用所需要的
 * requestHeader,replyHeader,request,response 则可以根据框架需要自定义扩展
 */
public class Packet {
    private int id;
    private PacketType packetType;
    private RequestHeader requestHeader;
    private ReplyHeader replyHeader;
    private Record request;
    private Record response;
    private boolean finished;
    private AsyncCallback callback;
    private static final AtomicInteger ID = new AtomicInteger(0);

    public Packet() {
        this.id = ID.incrementAndGet();
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

    public void encodeRequest(ByteBuf out, RecordEncoder requestEncoder) {
        out.writeInt(id);
        requestHeader.encode(out);
        requestEncoder.encode(request, out);
    }

    public void encodeResponse(ByteBuf out, RecordEncoder responseEncoder) {
        out.writeInt(id);
        replyHeader.encode(out);
        responseEncoder.encode(response, out);
    }

    public void decodeResponse(ByteBuf in, RecordDecoder responseDecoder) {
        this.id = in.readInt();
        this.replyHeader = new ReplyHeader();
        replyHeader.decode(in);
        this.response = responseDecoder.decode(in);
    }

    public void decodeRequest(ByteBuf in, RecordDecoder requestDecoder) {
        this.id = in.readInt();
        this.requestHeader = new RequestHeader();
        requestHeader.decode(in);
        this.request = requestDecoder.decode(in);
    }
}
