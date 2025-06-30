package com.shadril.email_api_demo.listener;

import com.shadril.email_api_demo.entity.HistoryLogEntity;
import com.shadril.email_api_demo.event.HistoryLogEvent;
import com.shadril.email_api_demo.service.BatchEmailLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailHistoryLogEventListener {

    private final BatchEmailLogService batchEmailLogService;

    @Async("emailLogExecutor")
    @EventListener
    public void handleEmailLogEvent(HistoryLogEvent event) {
        try {
            HistoryLogEntity historyLog = HistoryLogEntity.builder()
                    .subject(event.getEmailRequest().getSubject())
                    .toRecipients(event.getEmailRequest().getRecipients() != null ?
                            String.join(",", event.getEmailRequest().getRecipients()) : "")
                    .ccRecipients(event.getEmailRequest().getCcRecipients() != null ?
                            String.join(",", event.getEmailRequest().getCcRecipients()) : "")
                    .bccRecipients(event.getEmailRequest().getBccRecipients() != null ?
                            String.join(",", event.getEmailRequest().getBccRecipients()) : "")
                    .isScheduled(event.getEmailRequest().isScheduled())
                    .scheduleStartTime(event.getEmailRequest().getSchedulingDetails() != null ?
                            event.getEmailRequest().getSchedulingDetails().getStartTime() : null)
                    .scheduleEndTime(event.getEmailRequest().getSchedulingDetails() != null ?
                            event.getEmailRequest().getSchedulingDetails().getEndTime() : null)
                    .timeStamp(LocalDateTime.now())
                    .referenceId(event.getEmailRequest().getReferenceId() != null ? event.getEmailRequest().getReferenceId() : "")
                    .msgBody(event.getEmailRequest().getContent() != null ? event.getEmailRequest().getContent() : "")
                    .build();

            boolean queued = batchEmailLogService.queueLog(historyLog);
            if (!queued) {
                log.warn("Email log queue full, log entry discarded");
            }
        } catch (Exception e) {
            log.error("Failed to process email log: {}", e.getMessage(), e);
        }
    }
}

