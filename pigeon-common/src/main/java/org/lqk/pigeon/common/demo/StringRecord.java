package org.lqk.pigeon.common.demo;


import org.lqk.pigeon.proto.Record;


/**
 * Created by bert on 2017/3/22.
 */
public class StringRecord implements Record {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
