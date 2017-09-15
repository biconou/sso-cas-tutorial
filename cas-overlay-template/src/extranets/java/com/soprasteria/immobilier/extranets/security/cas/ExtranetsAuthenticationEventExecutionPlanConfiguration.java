package com.soprasteria.immobilier.extranets.security.cas;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("ExtranetsAuthenticationEventExecutionPlanConfiguration")
@EnableConfigurationProperties({CasConfigurationProperties.class, ExtranetsConfigurationProperties.class})
public class ExtranetsAuthenticationEventExecutionPlanConfiguration implements AuthenticationEventExecutionPlanConfigurer {

  Logger log = LoggerFactory.getLogger(ExtranetsAuthenticationEventExecutionPlanConfiguration.class);

  @Autowired
  private CasConfigurationProperties casProperties;

  @Autowired
  private ExtranetsConfigurationProperties extranetProperties;

  @Bean
  public AuthenticationHandler extranetsAuthenticationHandler() {

    log.info("Create a new AuthenticationHandler of type {}", ExtranetsAuthenticationHandler.class.getName());
    log.info("Authentication WebService URL is " + extranetProperties.getWsAuthenticationUrl());
    final ExtranetsAuthenticationHandler handler = new ExtranetsAuthenticationHandler(extranetProperties.getWsAuthenticationUrl());
        /*
            Configure the handler by invoking various setter methods.
            Note that you also have full access to the collection of resolved CAS settings.
            Note that each authentication handler may optionally qualify for an 'order`
            as well as a unique name.
        */
    return handler;
  }

  @Override
  public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
    plan.registerAuthenticationHandler(extranetsAuthenticationHandler());
  }
}