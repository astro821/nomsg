package com.makequest.nomsg.exception;

public class NoMsgNetworkException extends Exception {
    public NoMsgNetworkException() {
        super();
    }

    public NoMsgNetworkException(String message) {
        super(message);
    }

    public NoMsgNetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMsgNetworkException(Throwable cause) {
        super(cause);
    }

    protected NoMsgNetworkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
