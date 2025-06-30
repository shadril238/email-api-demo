package com.shadril.email_api_demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "email")
@Getter
@Setter
public class EmailConfig {
    private long maxPayloadSize;
    private int maxSubjectLength;
    private int maxRecipients;
    private int maxContentLength;
    private int maxContentTypeLength;
    private int maxFileNameLength;
    private int maxContentIdLength;
    private int maxCcRecipients;
    private int maxBccRecipients;
    private int maxFileAttachments;
    private int maxFileAttachmentSize;
    private int maxInlineAttachments;
    private int maxInlineAttachmentSize;
    private int totalRecipientLimit;
}

