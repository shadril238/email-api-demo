package com.shadril.email_api_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseDto<D, E> {
    private int statusCode;

    private long timestamp;

    private String message;

    private D data;

    private String resultCode;

    private E errors;

    private Map<String, Object> metadata = new HashMap<>();

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", timestamp='" + timestamp + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", resultCode=" + resultCode +
                ", errors=" + errors +
                ", metadata=" + metadata +
                '}';
    }
}