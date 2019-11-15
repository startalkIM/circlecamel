package com.qunar.qtalk.cricle.camel.common.exception;

/**
 * Created by haoling.wang on 2019/1/15.
 * <p>
 * 事件相关异常
 */
public class EventException extends RuntimeException {

    public EventException(String message) {
        super(message);
    }

    public EventException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventException(Throwable cause) {
        super(cause);
    }
}
