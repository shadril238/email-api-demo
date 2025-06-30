package com.shadril.email_api_demo.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InlineContentDto extends FileAttachmentDto {
    private String contentId;
    private String base64ContentAsDataUri;
}
