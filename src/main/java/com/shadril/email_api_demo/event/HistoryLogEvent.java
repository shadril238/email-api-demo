package com.shadril.email_api_demo.event;

import com.shadril.email_api_demo.dto.request.EmailDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryLogEvent {
    private EmailDto emailRequest;
}
