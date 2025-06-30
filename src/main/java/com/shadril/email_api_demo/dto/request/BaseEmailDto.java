package com.shadril.email_api_demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BaseEmailDto implements Serializable {

    @NotBlank(message = "Email Subject is required")
    @NotEmpty(message = "Email Subject is required")
    private String subject;

    @NotEmpty(message = "At least one recipient is required")
    private List<String> recipients;

    private List<String> ccRecipients;

    private List<String> bccRecipients;

    @NotBlank(message = "Email Content is required")
    private String content;

    @Valid
    private List<FileAttachmentDto> fileAttachments;

    @Valid
    private List<InlineContentDto> inlineContent;

}
