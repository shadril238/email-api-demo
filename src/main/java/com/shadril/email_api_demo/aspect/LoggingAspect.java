package com.shadril.email_api_demo.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* com.shadril.email_api_demo.producer.EmailMessageProducer.*(..)) || " +
            "execution(* com.shadril.email_api_demo.consumer.EmailMessageConsumer.*(..))")
    public Object logAroundMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        String threadName = Thread.currentThread().getName();

        Map<String, Object> logDetails = new HashMap<>();
        logDetails.put("thread", threadName);
        logDetails.put("method", joinPoint.getSignature().toShortString());
        logDetails.put("arguments", serializeArguments(joinPoint.getArgs()));

        log.info("Method Execution Started: {}", toJson(logDetails));

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            logDetails.put("exception", ex.getClass().getSimpleName() + ": " + ex.getMessage());
            log.error("Method Execution Failed: {}", toJson(logDetails), ex);
            throw ex;
        }

        long duration = (System.nanoTime() - startTime) / 1_000_000; // in ms
        logDetails.put("result", serializeResult(result));
        logDetails.put("executionTimeMs", duration);

        log.info("Method Execution Completed: {}", toJson(logDetails));
        return result;
    }

    private String serializeArguments(Object[] args) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            try {
                if (arg instanceof org.springframework.amqp.core.Message) {
                    builder.append("\"<Skipped org.springframework.amqp.core.Message>\"");
                } else {
                    builder.append(objectMapper.writeValueAsString(arg));
                }
            } catch (Exception e) {
                builder.append("{\"type\":\"")
                        .append(arg.getClass().getSimpleName())
                        .append("\",\"toString\":\"")
                        .append(safeToString(arg))
                        .append("\"}");
            }
            if (i < args.length - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private String serializeResult(Object result) {
        if (result == null) return "null";
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return "{\"type\":\"" + result.getClass().getSimpleName() +
                    "\",\"toString\":\"" + safeToString(result) + "\"}";
        }
    }

    private String toJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to serialize log details: " + e.getMessage() + "\"}";
        }
    }

    private String safeToString(Object obj) {
        try {
            return obj != null ? obj.toString().replace("\"", "'") : "null";
        } catch (Exception e) {
            return "unprintable";
        }
    }
}
