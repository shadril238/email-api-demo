package com.shadril.email_api_demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.util.Base64;

@Getter
@Builder
@RequiredArgsConstructor
public class FileDetails {
    private final String fileName;
    private final String mimeType;
    private final long size;
    private final ByteArrayResource resource;

    public String asBase64DataUri() {
        return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(resource.getByteArray());
    }
}