package org.lqk.pigeon.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * Created by bert on 2017/3/19.
 */
public class PigeonException extends Exception{
    private int err;

    public PigeonException(String message) {
        super(message);
    }

    public PigeonException(String format, Object... arguments) {
        super(MessageFormatter.arrayFormat(format, arguments).getMessage());
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
