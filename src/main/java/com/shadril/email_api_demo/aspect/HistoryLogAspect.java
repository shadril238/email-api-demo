package com.shadril.email_api_demo.aspect;

import com.shadril.email_api_demo.annotation.LogEmail;
import com.shadril.email_api_demo.dto.request.BaseEmailDto;
import com.shadril.email_api_demo.dto.request.EmailDto;
import com.shadril.email_api_demo.dto.request.EmailRequest;
import com.shadril.email_api_demo.dto.request.SchedulingDetailsDto;
import com.shadril.email_api_demo.event.HistoryLogEvent;
import com.shadril.email_api_demo.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class HistoryLogAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final AesUtil aesUtil;

    @Pointcut("@annotation(com.shadril.email_api_demo.annotation.LogEmail)")
    public void logEmailPointcut() {

    }

    @After("logEmailPointcut() && @annotation(logEmail)")
    public void afterLogEmail(JoinPoint joinPoint, LogEmail logEmail) {
        try {
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof EmailRequest emailRequest) {
                    String referenceId = emailRequest.getReferenceId();
                    Integer priority = emailRequest.getPriority();
                    boolean isScheduled = emailRequest.isScheduled();
                    SchedulingDetailsDto schedulingDetails = emailRequest.getSchedulingDetails();

                    for (BaseEmailDto baseEmailDto : emailRequest.getEmailRequests()) {
                        EmailDto emailDto = new EmailDto();
                        emailDto.setReferenceId(referenceId);
                        emailDto.setPriority(priority);
                        emailDto.setScheduled(isScheduled);
                        emailDto.setSchedulingDetails(schedulingDetails);
                        emailDto.setSubject(aesUtil.encrypt(baseEmailDto.getSubject()));
                        emailDto.setRecipients(baseEmailDto.getRecipients());
                        emailDto.setCcRecipients(baseEmailDto.getCcRecipients());
                        emailDto.setBccRecipients(baseEmailDto.getBccRecipients());
                        emailDto.setContent(aesUtil.encrypt(baseEmailDto.getContent()));
                        emailDto.setInlineContent(baseEmailDto.getInlineContent());
                        emailDto.setFileAttachments(baseEmailDto.getFileAttachments());

                        // Publish the event
                        eventPublisher.publishEvent(new HistoryLogEvent(emailDto));
                        log.debug("Published HistoryLogEvent for subject: {}, referenceId: {}",
                                emailDto.getSubject(), emailDto.getReferenceId());
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Failed to log email history via aspect: {}", e.getMessage(), e);
        }
    }
}
