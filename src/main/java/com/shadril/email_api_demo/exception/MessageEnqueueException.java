package com.shadril.email_api_demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class MessageEnqueueException extends RuntimeException {
    public MessageEnqueueException(String message) {
        super(message);
    }
}
