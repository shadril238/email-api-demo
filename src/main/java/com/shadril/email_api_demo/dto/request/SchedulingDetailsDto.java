package com.shadril.email_api_demo.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchedulingDetailsDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

