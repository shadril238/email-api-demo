package com.shadril.email_api_demo.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "smtp")
public class SmtpDetails {
    private String host;
    private String port;
    private String username;
    private String password;
    private String protocol;
    private boolean auth;
    private boolean tls;
    private String encoding;
}
