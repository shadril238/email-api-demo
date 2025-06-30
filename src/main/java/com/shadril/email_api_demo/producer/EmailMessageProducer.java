package com.shadril.email_api_demo.producer;

import com.shadril.email_api_demo.annotation.LogEmail;
import com.shadril.email_api_demo.config.RabbitMQConfig;
import com.shadril.email_api_demo.dto.request.BaseEmailDto;
import com.shadril.email_api_demo.dto.request.EmailDto;
import com.shadril.email_api_demo.dto.request.EmailRequest;
import com.shadril.email_api_demo.dto.request.SchedulingDetailsDto;
import com.shadril.email_api_demo.dto.response.ResponseDto;
import com.shadril.email_api_demo.exception.MessageEnqueueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailMessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;

    @LogEmail
    public void sendBulkEmailRequest(EmailRequest emailRequest) {
        String batchId = UUID.randomUUID().toString();
        String exchange = rabbitMQConfig.getEmailExchangeName();
        String referenceId = emailRequest.getReferenceId();
        Integer priority = emailRequest.getPriority();
        boolean isScheduled = emailRequest.isScheduled();
        SchedulingDetailsDto schedulingDetails = emailRequest.getSchedulingDetails();

        for (BaseEmailDto baseEmailDto : emailRequest.getEmailRequests()) {
            String msgTraceId = UUID.randomUUID().toString();
            EmailDto emailDto = new EmailDto();
            copyFields(baseEmailDto, emailDto);
            emailDto.setReferenceId(referenceId);
            emailDto.setPriority(priority);
            emailDto.setScheduled(isScheduled);
            emailDto.setSchedulingDetails(schedulingDetails);

            String routingKey = emailDto.isScheduled()
                    ? rabbitMQConfig.getEmailScheduledRoutingKeyName()
                    : rabbitMQConfig.getEmailRoutingKeyName();

            long delayMillis = emailDto.isScheduled()
                    ? Math.max(0, Duration.between(LocalDateTime.now(), emailDto.getSchedulingDetails().getStartTime()).toMillis())
                    : 0;

            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, emailDto, message -> {
                    message.getMessageProperties().setPriority(emailDto.getPriority());
                    message.getMessageProperties().setHeader("batchId", batchId);
                    message.getMessageProperties().setHeader("msgTraceId", msgTraceId);

                    if (delayMillis > 0) {
                        message.getMessageProperties().setExpiration(Long.toString(delayMillis));
                    }
                    return message;
                });

                ResponseDto<Object, Object> response = new ResponseDto<>();
                response.setStatusCode(HttpStatus.ACCEPTED.value());
                log.debug("Sent email request in producer end: {}", response);


            } catch (AmqpException e) {
                log.error("Failed to send bulk email request: {}", e.getMessage(), e);
                ResponseDto<Object, Object> response = new ResponseDto<>();
                response.setStatusCode(HttpStatus.BAD_GATEWAY.value());

                throw new MessageEnqueueException("Failed to enqueue message");
            }

            log.debug("Sent {} bulk email request with msgTraceId: {}",
                    emailDto.isScheduled() ? "scheduled " : "", msgTraceId);
        }
    }

    private static void copyFields(BaseEmailDto source, EmailDto target) {
        target.setSubject(source.getSubject());
        target.setRecipients(source.getRecipients());
        target.setCcRecipients(source.getCcRecipients());
        target.setBccRecipients(source.getBccRecipients());
        target.setContent(source.getContent());
        target.setInlineContent(source.getInlineContent());
        target.setFileAttachments(source.getFileAttachments());
    }
}