package com.shadril.email_api_demo.aspect;

import com.shadril.email_api_demo.annotation.RequireBasicAuth;
import com.shadril.email_api_demo.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Aspect
@Component
@RequiredArgsConstructor
public class BasicAuthAspect {

    private final HttpServletRequest request;

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    @Before("@annotation(requireBasicAuth)")
    public void checkBasicAuth(RequireBasicAuth requireBasicAuth) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new UnauthorizedAccessException("Missing or invalid Authorization header");
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

        String[] values = credentials.split(":", 2);
        if (values.length != 2 || !USERNAME.equals(values[0]) || !PASSWORD.equals(values[1])) {
            throw new UnauthorizedAccessException("Invalid username or password");
        }
    }
}
