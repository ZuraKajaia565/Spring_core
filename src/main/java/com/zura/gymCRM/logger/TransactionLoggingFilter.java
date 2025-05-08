package com.zura.gymCRM.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionLoggingFilter.class);
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Check if we already have a transaction ID (might be passed from upstream service)
        String transactionId = request.getHeader("X-Transaction-ID");

        // If no transaction ID exists, generate a new one
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }

        // Set the transaction ID in MDC
        MDC.put(TRANSACTION_ID, transactionId);

        // Set the transaction ID in the response header for downstream services
        response.setHeader("X-Transaction-ID", transactionId);

        try {
            // Log the incoming request (but don't include sensitive information)
            logger.info("Received {} request to {}",
                    request.getMethod(),
                    request.getRequestURI());

            // Proceed with the request
            filterChain.doFilter(request, response);

            // Log the outgoing response
            logger.info("Completed request with status {}",
                    response.getStatus());
        } finally {
            // Clear MDC
            MDC.clear();
        }
    }
}