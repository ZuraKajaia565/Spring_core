package com.zura.gymCRM.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that adds a transaction ID to each request and logs request details.
 * Places the transaction ID in the MDC for logging and adds it to response headers.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionLoggingFilter.class);
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Check if transaction ID already exists (might be from upstream service)
        String transactionId = request.getHeader("X-Transaction-ID");

        // If no transaction ID exists, generate a new one
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }

        // Set the transaction ID in MDC for logging
        MDC.put(TRANSACTION_ID, transactionId);

        // Set the transaction ID in the response header for downstream services
        response.setHeader("X-Transaction-ID", transactionId);

        try {
            // Log the incoming request
            logger.info("Received {} request to {} from {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());

            // Proceed with the filter chain
            filterChain.doFilter(request, response);

            // Log the outgoing response
            logger.info("Completed request with status {}",
                    response.getStatus());
        } finally {
            // Always clear MDC to prevent memory leaks
            MDC.clear();
        }
    }
}