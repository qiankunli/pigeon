package org.lqk.pigeon.common.proto;

import io.netty.buffer.ByteBuf;
import org.lqk.pigeon.codec.RecordDecoder;
import org.lqk.pigeon.codec.RecordEncoder;
import org.lqk.pigeon.exception.PigeonException;
import org.lqk.pigeon.proto.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bert on 2017/3/19.
 * <p>
 * id,packetType 是为了确保连接可用所需要的
 * requestHeader,replyHeader,request,response 则可以根据框架需要自定义扩展
 */
public class Packet {
    private PacketType packetType;  /* 必须优先被序列化和反序列化，因为系统还会发送心跳包*/
    private int id;
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

    public PacketType getPacketType() {
        return packetType;
    }

    public void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }

    public AsyncCallback getCallback() {
        return callback;
    }

    public void setCallback(AsyncCallback callback) {
        this.callback = callback;
    }

    public void encodeRequest(ByteBuf out, RecordEncoder requestEncoder) throws PigeonException {
        out.writeInt(PacketType.cs.getValue());
        out.writeInt(id);
        if (null == requestHeader) {
            throw new PigeonException("requestHeader can not be null");
        }
        requestHeader.encode(out);
        if (null == request) {
            throw new PigeonException("request can not be null");
        }
        requestEncoder.encode(request, out);
    }

    public void encodeResponse(ByteBuf out, RecordEncoder responseEncoder) throws PigeonException {
        out.writeInt(PacketType.ss.getValue());
        out.writeInt(id);
        if (null == replyHeader) {
            throw new PigeonException("replyHeader can not be null");
        }
        replyHeader.encode(out);
        if (null == response) {
            throw new PigeonException("response can not be null");
        }
        responseEncoder.encode(response, out);
    }

    public void decodeResponse(ByteBuf in, RecordDecoder responseDecoder) {
        this.packetType = PacketType.valueOf(in.readInt());
        this.id = in.readInt();
        this.replyHeader = new ReplyHeader();
        replyHeader.decode(in);
        this.response = responseDecoder.decode(in);
    }

    public void decodeRequest(ByteBuf in, RecordDecoder requestDecoder) {
        this.packetType = PacketType.valueOf(in.readInt());
        this.id = in.readInt();
        this.requestHeader = new RequestHeader();
        requestHeader.decode(in);
        this.request = requestDecoder.decode(in);
    }
}
