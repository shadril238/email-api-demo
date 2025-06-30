package com.shadril.email_api_demo.exception;

public class FileConversionException extends RuntimeException{
    public FileConversionException(String message) {
        super(message);
    }

    public FileConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
