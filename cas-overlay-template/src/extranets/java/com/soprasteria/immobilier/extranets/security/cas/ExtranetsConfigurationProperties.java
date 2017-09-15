/**
 * Paquet de définition
 **/
package com.soprasteria.immobilier.extranets.security.cas;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/

@ConfigurationProperties(prefix = "com.soprasteria.immobilier.extranets")
public class ExtranetsConfigurationProperties {

  private String wsAuthenticationUrl = null;


  public String getWsAuthenticationUrl() {
    return wsAuthenticationUrl;
  }

  public void setWsAuthenticationUrl(String wsAuthenticationUrl) {
    this.wsAuthenticationUrl = wsAuthenticationUrl;
  }
}
 
