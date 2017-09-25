/**
 * Paquet de définition
 **/
package com.github.biconou.sso.cas.tutorial;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
@Controller
public class ForwardToSample2Controller {

  @Value("${authenticationFilter.serverName}")
  private String serverName = null;


  @RequestMapping("/forwardToSample2")
  public String forward() throws Exception {

    String CASUrl = "https://localhost:8443/cas/";
    String serviceName = "http://localhost:8082/sample2/";
    String TGT = CASUtils.generateTGT(CASUrl, "CASTOI", "maisnon!");
    String ST = CASUtils.obtainServiceTicket(CASUrl, TGT, serviceName);

    String location = "http://localhost:8082/sample2?ticket=" + ST;
    return "redirect:" + location;
  }

}
 
