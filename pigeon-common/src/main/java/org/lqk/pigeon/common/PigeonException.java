package org.lqk.pigeon.common;

/**
 * Created by bert on 2017/3/19.
 */
public class PigeonException extends Exception{
    private int err;

    public PigeonException(int err) {
        this.err = err;
    }

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }
}
