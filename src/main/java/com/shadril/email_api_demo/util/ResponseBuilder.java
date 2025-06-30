package com.shadril.email_api_demo.util;

import com.shadril.email_api_demo.dto.response.ResponseDto;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@NoArgsConstructor
public class ResponseBuilder {

    public static <D, E> ResponseEntity<ResponseDto<D, E>> success(int statusCode, String message, D data) {
        return buildResponseEntity(ResponseDto.<D, E>builder()
                .statusCode(statusCode)
                .timestamp(System.currentTimeMillis())
                .message(message)
                .data(data)
                .build());
    }

    public static <D, E> ResponseEntity<ResponseDto<D, E>> success(int statusCode, String resultCode, String message, D data) {
        return buildResponseEntity(ResponseDto.<D, E>builder()
                .statusCode(statusCode)
                .timestamp(System.currentTimeMillis())
                .message(message)
                .data(data)
                .build());
    }

    public static <D, E> ResponseEntity<ResponseDto<D, E>> success(int statusCode, String message) {
        return buildResponseEntity(ResponseDto.<D, E>builder()
                .statusCode(statusCode)
                .timestamp(System.currentTimeMillis())
                .message(message)
                .build());
    }

    public static <D, E> ResponseEntity<ResponseDto<D, E>> success(int statusCode, String message, String resultCode) {
        return buildResponseEntity(ResponseDto.<D, E>builder()
                .statusCode(statusCode)
                .timestamp(System.currentTimeMillis())
                .resultCode(resultCode)
                .message(message)
                .build());
    }

    public static <D, E> ResponseEntity<ResponseDto<D, E>> error(int statusCode, String message, E errors) {
        return buildResponseEntity(ResponseDto.<D, E>builder()
                .statusCode(statusCode)
                .message(message)
                .errors(errors)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public static <D, E> ResponseEntity<ResponseDto<D, E>> error(int statusCode, String message, String resultCode, E errors) {
        return buildResponseEntity(ResponseDto.<D, E>builder()
                .statusCode(statusCode)
                .message(message)
                .resultCode(resultCode)
                .errors(errors)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public static <D, E> ResponseEntity<ResponseDto<D, E>> error(int statusCode, String message) {
        return buildResponseEntity(ResponseDto.<D, E>builder()
                .statusCode(statusCode)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public static <D, E> ResponseEntity<ResponseDto<D, E>> error(int statusCode, String message, String resultCode) {
        return buildResponseEntity(ResponseDto.<D, E>builder()
                .statusCode(statusCode)
                .message(message)
                .resultCode(resultCode)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    public static <D, E> ResponseEntity<ResponseDto<D, E>> buildResponseEntity(ResponseDto<D, E> response) {
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    public static <D, E> void withMetadata(ResponseEntity<ResponseDto<D, E>> response, String key, Object value) {
        Objects.requireNonNull(response.getBody()).addMetadata(key, value);
    }
}