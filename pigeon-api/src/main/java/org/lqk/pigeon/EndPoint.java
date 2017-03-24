package org.lqk.pigeon;

/**
 * Created by bert on 2017/3/24.
 *
 * 公共接口，定义通用操作
 */
public interface EndPoint {
    void open() throws Exception;
    void close() throws Exception;
}
