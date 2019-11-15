package com.qunar.qtalk.cricle.camel.common.exception;

/**
 * Created by haoling.wang on 2018/12/28.
 */
public class RedisOpsException extends RuntimeException {

    public RedisOpsException() {
        super();
    }

    public RedisOpsException(String message) {
        super(message);
    }

    public RedisOpsException(Throwable cause) {
        super(cause);
    }
}
