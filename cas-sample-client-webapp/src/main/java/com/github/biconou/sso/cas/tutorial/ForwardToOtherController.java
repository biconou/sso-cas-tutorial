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
public class ForwardToOtherController {

  @Value("${forwardToOtherController.casurl}")
  private String CASUrl = null;

  @Value("${forwardToOtherController.serviceName}")
  private String serviceName = null;

  @Value("${forwardToOtherController.forwardLocation}")
  private String forwardLocation = null;

  @RequestMapping("/forwardToOther")
  public String forward() throws Exception {

    String TGT = CASUtils.generateTGT(CASUrl, "CASTOI", "maisnon!");
    String ST = CASUtils.obtainServiceTicket(CASUrl, TGT, serviceName);

    String location = forwardLocation + "?ticket=" + ST;
    return "redirect:" + location;
  }

}
 
