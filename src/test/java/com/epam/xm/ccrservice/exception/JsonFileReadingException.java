package com.epam.xm.ccrservice.exception;

public class JsonFileReadingException extends RuntimeException {
    public JsonFileReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
