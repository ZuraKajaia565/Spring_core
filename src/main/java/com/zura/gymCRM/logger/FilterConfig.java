package com.zura.gymCRM.logger;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<TransactionLoggingFilter> loggingFilter() {
        FilterRegistrationBean<TransactionLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TransactionLoggingFilter());
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
