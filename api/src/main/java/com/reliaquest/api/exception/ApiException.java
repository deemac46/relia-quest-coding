package com.reliaquest.api.exception;

/*
    * Custom exception class for error API error handling.
 */
public class ApiException extends Exception {

    private final int statusCode;
    public ApiException(String message, int statusCode, Throwable cause) {
        super(message);
        this.statusCode = statusCode;
    }

    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
