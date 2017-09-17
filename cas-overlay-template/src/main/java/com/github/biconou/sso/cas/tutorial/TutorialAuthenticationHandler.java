package com.github.biconou.sso.cas.tutorial; /**
 * Paquet de définition
 **/

import java.security.GeneralSecurityException;
import javax.security.auth.login.FailedLoginException;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class TutorialAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

  Logger log = LoggerFactory.getLogger(TutorialAuthenticationHandler.class);

  public TutorialAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
    super(name, servicesManager, principalFactory, order);
  }


  @Override
  protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String s) throws GeneralSecurityException, PreventedException {

    String username = credential.getUsername().toUpperCase();

    if ("CASTOI".equals(credential.getUsername().toUpperCase()) && "maisnon!".equals(credential.getPassword())) {
      return createHandlerResult(credential,
        this.principalFactory.createPrincipal(username), null);
    }
    else {
      throw new FailedLoginException("Sorry, you are a failure!");
    }
  }
}
 
