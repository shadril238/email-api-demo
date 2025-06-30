package com.shadril.email_api_demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileAttachmentResponse {
    private String base64ContentAsDataUri;
    private String fileNameWithoutExtension;
}
