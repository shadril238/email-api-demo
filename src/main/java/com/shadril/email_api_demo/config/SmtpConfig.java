package com.shadril.email_api_demo.config;

import com.shadril.email_api_demo.dto.SmtpDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


@Configuration
@RequiredArgsConstructor
public class SmtpConfig {

    private final SmtpDetails smtpDetails;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpDetails.getHost());
        mailSender.setPort(Integer.parseInt(smtpDetails.getPort()));
        mailSender.setUsername(smtpDetails.getUsername());
        mailSender.setPassword(smtpDetails.getPassword());
        mailSender.setDefaultEncoding(smtpDetails.getEncoding());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", smtpDetails.getProtocol());
        props.put("mail.smtp.auth", String.valueOf(smtpDetails.isAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(smtpDetails.isTls()));
        props.put("mail.smtp.connectiontimeout", 5000);
        props.put("mail.smtp.timeout", 5000);
        props.put("mail.smtp.writetimeout", 5000);
//        props.put("mail.debug", "true");

        return mailSender;
    }
}