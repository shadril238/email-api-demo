package com.shadril.email_api_demo.controller;

import com.shadril.email_api_demo.annotation.RequireBasicAuth;
import com.shadril.email_api_demo.dto.request.EmailRequest;
import com.shadril.email_api_demo.dto.response.ResponseDto;
import com.shadril.email_api_demo.producer.EmailMessageProducer;
import com.shadril.email_api_demo.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email/v1/send")
public class EmailController {

    private final EmailMessageProducer emailProducer;

    @PostMapping
    @RequireBasicAuth
    public ResponseEntity<ResponseDto<Object, List<Map<String, String>>>> sendEmail(
            @Valid @RequestBody EmailRequest emailRequest) {

        emailProducer.sendBulkEmailRequest(emailRequest);
        return ResponseBuilder.success(ACCEPTED.value(), "Email request accepted for processing", ACCEPTED);
    }
}
