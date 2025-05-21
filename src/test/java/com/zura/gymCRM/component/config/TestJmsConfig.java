// src/test/java/com/example/micro/config/TestJmsConfig.java
package com.zura.gymCRM.component.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import jakarta.jms.ConnectionFactory;

@Configuration
@Profile("test")
public class TestJmsConfig {
    @Bean @Primary
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("vm://localhost?broker.persistent=false");
        factory.setTrustAllPackages(true);
        return factory;
    }

    @Bean @Primary
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }
}