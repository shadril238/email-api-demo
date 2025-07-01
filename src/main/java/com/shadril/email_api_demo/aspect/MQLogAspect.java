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
public class MQLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* com.shadril.email_api_demo.producer..*(..)) || execution(* com.shadril.email_api_demo.consumer..*(..))")
    public Object logAroundExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        String threadName = Thread.currentThread().getName();

        Map<String, Object> logDetails = new HashMap<>();
        logDetails.put("thread", threadName);
        logDetails.put("method", joinPoint.getSignature().toShortString());

        try {
            logDetails.put("arguments", serializeArguments(joinPoint.getArgs()));
        } catch (Exception e) {
            logDetails.put("arguments", "Argument serialization failed: " + e.getMessage());
        }

        log.info("Method Execution Started: {}", toJson(logDetails));

        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            logDetails.put("exception", ex.getClass().getSimpleName() + ": " + ex.getMessage());
            log.error("Method Execution Failed: {}", toJson(logDetails), ex);
            throw ex;
        } finally {
            long duration = (System.nanoTime() - startTime) / 1_000_000; // ms
            logDetails.put("executionTimeMs", duration);
            logDetails.put("result", serializeResult(result));

            log.info("Method Execution Completed: {}", toJson(logDetails));
        }
    }

    private String serializeArguments(Object[] args) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof org.springframework.amqp.core.Message) {
                sb.append("\"<Skipped: org.springframework.amqp.core.Message>\"");
            } else {
                try {
                    sb.append(objectMapper.writeValueAsString(arg));
                } catch (Exception e) {
                    sb.append("{\"type\":\"")
                            .append(arg != null ? arg.getClass().getSimpleName() : "null")
                            .append("\",\"toString\":\"")
                            .append(safeToString(arg))
                            .append("\"}");
                }
            }
            if (i < args.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String serializeResult(Object result) {
        if (result == null) return "null";
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return "{\"type\":\"" + result.getClass().getSimpleName() +
                    "\",\"toString\":\"" + safeToString(result) + "\"}";
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize to JSON: " + e.getMessage() + "\"}";
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