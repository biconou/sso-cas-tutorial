package com.github.biconou.sso.cas.tutorial;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Value("${authenticationFilter.serverName}")
    private String serverName = null;

    @Value("${authenticationFilter.casServerLoginUrl}")
    private String casServerLoginUrl = null;


    @Bean
    public FilterRegistrationBean CASAuthenticationFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AuthenticationFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("casServerLoginUrl", casServerLoginUrl);
        registration.addInitParameter("serverName", serverName);
        registration.setName("CAS Authentication Filter");
        registration.setOrder(1);
        return registration;
    }

    @Value("${validationFilter.casServerUrlPrefix}")
    private String casServerUrlPrefix = null;

    @Bean
    public FilterRegistrationBean CASValidationFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("casServerUrlPrefix", casServerUrlPrefix);
        registration.addInitParameter("serverName", serverName);
        registration.addInitParameter("redirectAfterValidation", "true");
        registration.addInitParameter("useSession", "true");
        registration.addInitParameter("authn_method", "mfa-duo");
        registration.setName("CAS Validation Filter");
        registration.setOrder(2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean CASHttpServletRequestWrapperFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestWrapperFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(3);
        return registration;
    }

}
