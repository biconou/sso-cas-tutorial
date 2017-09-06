/**
 * Paquet de définition
 **/
package tutorial;

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
    log.info("Test if credentials equals to CASTOI" + username);

    if ("CASTOI".equals(credential.getUsername().toUpperCase())) {
      return createHandlerResult(credential,
        this.principalFactory.createPrincipal(username), null);
    }
    throw new FailedLoginException("Sorry, you are a failure!");
  }
}
 
