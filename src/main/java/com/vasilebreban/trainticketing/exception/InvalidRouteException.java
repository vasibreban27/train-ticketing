package com.vasilebreban.trainticketing.exception;

public class InvalidRouteException extends RuntimeException {

    public InvalidRouteException(String message) {
        super(message);
    }
}