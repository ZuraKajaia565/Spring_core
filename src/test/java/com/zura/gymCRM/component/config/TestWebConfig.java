package com.zura.gymCRM.component.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@ActiveProfiles("test")
public class TestWebConfig {

    /**
     * Creates a MockMvc bean that can be autowired in test classes
     */
    @Bean
    public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
        return MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }
}