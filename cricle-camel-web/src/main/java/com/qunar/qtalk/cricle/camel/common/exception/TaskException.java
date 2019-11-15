package com.qunar.qtalk.cricle.camel.common.exception;

/**
 * Created by haoling.wang on 2019/1/10.
 */
public class TaskException extends RuntimeException {

    public TaskException() {
        super();
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(Throwable cause) {
        super(cause);
    }
}
