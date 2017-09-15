package tutorial;

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

@Configuration("TutorialAuthenticationEventExecutionPlanConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class TutorialAuthenticationEventExecutionPlanConfiguration implements AuthenticationEventExecutionPlanConfigurer {

  Logger log = LoggerFactory.getLogger(TutorialAuthenticationEventExecutionPlanConfiguration.class);

  @Autowired
  private CasConfigurationProperties casProperties;

  @Bean
  public AuthenticationHandler tutorialAuthenticationHandler() {

    log.info("Create a new AuthenticationHandler of type {}", TutorialAuthenticationHandler.class.getName());
    final TutorialAuthenticationHandler handler = new TutorialAuthenticationHandler("tutorialAuthenticationHandler", null, null, null);
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
    plan.registerAuthenticationHandler(tutorialAuthenticationHandler());
  }
}