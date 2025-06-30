package com.shadril.email_api_demo.exception;

import com.shadril.email_api_demo.dto.response.ResponseDto;
import com.shadril.email_api_demo.util.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileConversionException.class)
    public ResponseEntity<ResponseDto<String, String>> handleFileConversionException(FileConversionException ex) {
        log.error("File conversion error: {}", ex.getMessage());
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(MessageEnqueueException.class)
    public ResponseEntity<ResponseDto<String, String>> handleMessageEnqueueException(MessageEnqueueException ex) {
        log.error("Message enqueue error: {}", ex.getMessage());
        return ResponseBuilder.error(HttpStatus.BAD_GATEWAY.value(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<String, String>> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
}
