package com.powerclowd.forecast.exception;


public class InternalErrorException extends RuntimeException {

    public InternalErrorException(String message, Throwable cause) {
        super (message, cause);
    }
}
