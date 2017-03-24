package org.lqk.pigeon.common;

/**
 * Created by bert on 2017/3/19.
 */
public class PigeonException extends Exception{
    private int err;

    public PigeonException(String message) {
        super(message);
    }

    public PigeonException(String message, Throwable cause) {
        super(message, cause);
    }

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
