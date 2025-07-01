package com.shadril.email_api_demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AesConfig {
    @Value("${encryption.aes.key}")
    private String key;

    public void setKey(String key) {
        if (key.length() != 32) {
            throw new IllegalArgumentException("AES key must be 32 characters for 256-bit encryption");
        }
        this.key = key;
    }
}
