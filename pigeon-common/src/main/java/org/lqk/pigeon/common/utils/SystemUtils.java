package org.lqk.pigeon.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by bert on 2017/3/19.
 */
public class SystemUtils {
    private static Logger log = LoggerFactory.getLogger(SystemUtils.class);

    public static InetAddress getInetAddress() {

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("unknown host!");
        }
        return null;

    }

    public static String makeThreadName(String suffix) {
        String name = Thread.currentThread().getName().
                replaceAll("-EventThread", "");
        return name + suffix;
    }
}
