package com.zura.gymCRM.messaging;

import com.zura.gymCRM.config.JmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkloadMessageProducer {
    private static final Logger logger = LoggerFactory.getLogger(WorkloadMessageProducer.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendWorkloadMessage(WorkloadMessage message) {
        logger.info("Sending message to workload queue: {}", message);
        try {
            jmsTemplate.convertAndSend(JmsConfig.WORKLOAD_QUEUE, message);
            logger.info("Message sent successfully, transaction ID: {}", message.getTransactionId());
        } catch (Exception e) {
            logger.error("Failed to send message: {}", e.getMessage(), e);
            throw e;
        }
    }
}