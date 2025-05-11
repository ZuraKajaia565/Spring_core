package com.zura.gymCRM.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class JmsConfig {

    public static final String WORKLOAD_QUEUE = "workload-queue";
    public static final String WORKLOAD_DLQ = "workload-dlq";

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String username;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("${spring.activemq.packages.trust-all:false}")
    private boolean trustAllPackages;

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        factory.setUserName(username);
        factory.setPassword(password);
        factory.setTrustAllPackages(trustAllPackages);

        // Configure redelivery policy
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(3);
        redeliveryPolicy.setInitialRedeliveryDelay(1000);
        redeliveryPolicy.setBackOffMultiplier(2);
        redeliveryPolicy.setUseExponentialBackOff(true);
        factory.setRedeliveryPolicy(redeliveryPolicy);

        return factory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(connectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate(cachingConnectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        template.setDeliveryPersistent(true);
        template.setSessionTransacted(true);
        return template;
    }

    // Different connection configuration for production environment
    @Configuration
    @Profile("prod")
    public static class ProdJmsConfig {
        @Value("${spring.activemq.prod.broker-url}")
        private String prodBrokerUrl;

        @Value("${spring.activemq.prod.user}")
        private String prodUsername;

        @Value("${spring.activemq.prod.password}")
        private String prodPassword;

        @Bean
        public ActiveMQConnectionFactory connectionFactory() {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
            factory.setBrokerURL(prodBrokerUrl);
            factory.setUserName(prodUsername);
            factory.setPassword(prodPassword);
            factory.setTrustAllPackages(false);

            // Production-specific redelivery policy
            RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
            redeliveryPolicy.setMaximumRedeliveries(5);
            redeliveryPolicy.setInitialRedeliveryDelay(5000);
            redeliveryPolicy.setBackOffMultiplier(2);
            redeliveryPolicy.setUseExponentialBackOff(true);
            factory.setRedeliveryPolicy(redeliveryPolicy);

            return factory;
        }
    }

    // Development environment configuration
    @Configuration
    @Profile("dev")
    public static class DevJmsConfig {
        @Value("${spring.activemq.dev.broker-url:tcp://localhost:61616}")
        private String devBrokerUrl;

        @Bean
        public ActiveMQConnectionFactory connectionFactory() {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
            factory.setBrokerURL(devBrokerUrl);
            factory.setTrustAllPackages(true); // Less strict for development

            return factory;
        }
    }
}