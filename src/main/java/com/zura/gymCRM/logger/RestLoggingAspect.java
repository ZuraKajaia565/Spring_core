package com.zura.gymCRM.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class RestLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(RestLoggingAspect.class);

    @Before("execution(* com.zura.gymCRM.controller..*(..))")
    public void logRequest(JoinPoint joinPoint) {
        String transactionId = MDC.get("transactionId");
        logger.info("Transaction ID: {} - Incoming request to {} with arguments: {}",
                transactionId,
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* com.zura.gymCRM.controller..*(..))", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        String transactionId = MDC.get("transactionId");
        if (result instanceof ResponseEntity<?> responseEntity) {
            logger.info("Transaction ID: {} - Response from {}: Status={}, Body={}",
                    transactionId,
                    joinPoint.getSignature().toShortString(),
                    responseEntity.getStatusCode(),
                    responseEntity.getBody());
        } else {
            logger.info("Transaction ID: {} - Response from {}: Body={}",
                    transactionId,
                    joinPoint.getSignature().toShortString(),
                    result);
        }
    }
}
