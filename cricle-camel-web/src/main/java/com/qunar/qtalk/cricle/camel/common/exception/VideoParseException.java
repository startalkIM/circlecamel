package com.qunar.qtalk.cricle.camel.common.exception;

public class VideoParseException extends RuntimeException {

    public VideoParseException() {
        super();
    }

    public VideoParseException(String message) {
        super(message);
    }

    public VideoParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public VideoParseException(Throwable cause) {
        super(cause);
    }
}
