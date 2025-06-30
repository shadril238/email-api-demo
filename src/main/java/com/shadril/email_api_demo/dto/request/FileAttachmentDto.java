package com.shadril.email_api_demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAttachmentDto {
    private String base64ContentAsDataUri;
    private String fileNameWithoutExtension;
}
