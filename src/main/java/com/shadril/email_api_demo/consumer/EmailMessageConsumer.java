package com.shadril.email_api_demo.consumer;

import com.rabbitmq.client.Channel;
import com.shadril.email_api_demo.config.RabbitMQConfig;
import com.shadril.email_api_demo.dto.SmtpDetails;
import com.shadril.email_api_demo.dto.request.EmailDto;
import com.shadril.email_api_demo.dto.response.ResponseDto;
import com.shadril.email_api_demo.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailMessageConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;
    private final SmtpDetails smtpDetails;
    private final EmailUtil emailUtil;

    @RabbitListener(queues = "${rabbitmq.config.email-queue}", concurrency = "20-20", executor = "configurableThreadExecutor")
    public void receiveMessage(Message message, Channel channel) {
        try {
            processMessage(message, channel);
        } catch (IOException e) {
            log.error("Error processing message", e);
        }
    }

    private void processMessage(Message message, Channel channel) throws IOException {
        log.debug("Received message in consumer end: {}", message);
        String msgTraceId = (String) message.getMessageProperties().getHeaders().get("msgTraceId");

        try {
            EmailDto emailRequest = (EmailDto) rabbitTemplate.getMessageConverter().fromMessage(message);

            try {
                sendEmail(emailRequest);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

                ResponseDto<Object, Object> response = new ResponseDto<>();
                response.setMessage("Email send successfully");
                log.info("Email sent via EmailUtil: {}", emailRequest.getSubject());
                response.setStatusCode(HttpStatus.OK.value());

            } catch (MailSendException ex) {

                Map<String, Object> headers = message.getMessageProperties().getHeaders();
                long retryCount = headers.get("retryCount") != null ? (Long) headers.get("retryCount") : 0L;

                if (retryCount >= rabbitMQConfig.getEmailMaxRetries()) {
                    rabbitTemplate.convertAndSend(rabbitMQConfig.getEmailDLXName(), rabbitMQConfig.getEmailDLQRoutingKeyName(), message);
                    log.debug("Message {} sent to DLQ after {} retries", emailRequest.getSubject(), retryCount);

                    ResponseDto<Object, Object> response = new ResponseDto<>();
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

                } else {
                    message.getMessageProperties().setHeader("retryCount", retryCount + 1);
                    rabbitTemplate.convertAndSend(rabbitMQConfig.getEmailDLXName(), rabbitMQConfig.getEmailRetryRoutingKeyName(), message);
                    log.debug("Message {} sent to retry queue, retry count: {}", emailRequest.getSubject(), retryCount + 1);
                }
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (MailSendException | MessageConversionException err) {
            log.debug("Failed to send Email : {} ", message, err);
            rabbitTemplate.convertAndSend(rabbitMQConfig.getEmailDLXName(), rabbitMQConfig.getEmailDLQRoutingKeyName(), message);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    public void sendEmail(EmailDto emailRequest) {
        try {
            emailUtil.sendMimeMail(emailRequest, smtpDetails.getUsername());
            log.info("Email sent successfully: {}", emailRequest.getSubject());
        } catch (MailSendException e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
            throw new MailSendException("Failed to send email", e);
        }
    }
}
