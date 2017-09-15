/**
 * Paquet de définition
 **/
package com.soprasteria.immobilier.extranets.security.cas;

import java.security.GeneralSecurityException;
import javax.security.auth.login.FailedLoginException;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sopra.immobilier.ws.core.WSAuthenticationClient;
import sopra.immobilier.ws.core.WSAuthenticationMessage;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class ExtranetsAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

  Logger log = LoggerFactory.getLogger(ExtranetsAuthenticationHandler.class);

  private WSAuthenticationClient wsAuthenticationClient = null;

  public ExtranetsAuthenticationHandler(String wsAuthenticationUrl) {
    super("extranetsAuthenticationHandler", null, null, null);

    wsAuthenticationClient = new WSAuthenticationClient();
    wsAuthenticationClient.setURLStart(wsAuthenticationUrl);
    wsAuthenticationClient.setValidity(1);
  }


  @Override
  protected HandlerResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String s) throws GeneralSecurityException, PreventedException {

    String username = credential.getUsername().toUpperCase();
    log.debug("Authenticating use {}" + username);

    wsAuthenticationClient.setUserID(credential.getUsername());
    wsAuthenticationClient.setUserPWD(credential.getPassword());
    boolean authenticated = wsAuthenticationClient.authenticate().getStatut().equals(WSAuthenticationMessage.Statut.AUTHENTICATED);

    if (authenticated) {
      return createHandlerResult(credential,
        this.principalFactory.createPrincipal(username), null);
    }
    else {
      throw new FailedLoginException("User " + username + " has not been authenticated");
    }
  }
}
 
