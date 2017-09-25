/**
 * Paquet de définition
 **/

import org.junit.Test;
import com.github.biconou.sso.cas.tutorial.CASUtils;

/**
 * Description: Merci de donner une description du service rendu par cette classe
 **/
public class CASUtilsTest {

  @Test
  public void generateTGT() throws Exception {

    System.setProperty("javax.net.ssl.trustStore", "D:/entorno/ide/workspace/sso-cas/dist/tutorial/thekeystore-localhost.jks");

    String CASUrl = "https://localhost:8443/cas/";
    String TGT = CASUtils.generateTGT(CASUrl, "CASTOI", "maisnon!");
    System.out.print(TGT);

  }

  @Test
  public void obtainServiceTicket() throws Exception {

    System.setProperty("javax.net.ssl.trustStore", "D:/entorno/ide/workspace/sso-cas/dist/tutorial/thekeystore-localhost.jks");

    String CASUrl = "https://localhost:8443/cas/";
    String serviceName = "http://localhost:8082/sample2/";
    String TGT = CASUtils.generateTGT(CASUrl, "CASTOI", "maisnon!");
    String ST = CASUtils.obtainServiceTicket(CASUrl, TGT, serviceName);
    System.out.print(ST);

  }


}
 
