/**
 * Paquet de définition
 **/
package com.github.biconou.sso.cas.tutorial;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CASUtils {


  public static String generateTGT(String casApplicationUrl, String username, String password) throws Exception {
    StringBuilder CASRestTicketUrlString = new StringBuilder(casApplicationUrl);

    if (!casApplicationUrl.endsWith("/")) {
      CASRestTicketUrlString.append("/");
    }
    CASRestTicketUrlString.append("v1/tickets");


    URL urlCASRestTicket = new URL(CASRestTicketUrlString.toString());
    HttpURLConnection urlConnection = (HttpURLConnection) urlCASRestTicket.openConnection();
    urlConnection.setRequestMethod("POST");
    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    urlConnection.setDoOutput(true);

    OutputStream os = urlConnection.getOutputStream();
    StringBuilder content = new StringBuilder();
    content.append("username=").append(URLEncoder.encode(username, "UTF-8"));
    content.append("&");
    content.append("password=").append(URLEncoder.encode(password, "UTF-8"));
    os.write(content.toString().getBytes());
    os.close();

    String locationHeaderValue = urlConnection.getHeaderField("Location");
    Pattern pattern = Pattern.compile("(.*)(TGT-.*)");
    Matcher m = pattern.matcher(locationHeaderValue);
    m.matches();

    return m.group(2);
  }

  /**
   * @param casApplicationUrl
   * @param TGT
   * @param serviceName
   * @return
   */
  public static String obtainServiceTicket(String casApplicationUrl, String TGT, String serviceName) throws Exception {

    StringBuilder CASRestTicketUrlString = new StringBuilder(casApplicationUrl);

    if (!casApplicationUrl.endsWith("/")) {
      CASRestTicketUrlString.append("/");
    }
    CASRestTicketUrlString.append("v1/tickets/");
    CASRestTicketUrlString.append(TGT);


    URL urlCASRestTicket = new URL(CASRestTicketUrlString.toString());
    HttpURLConnection urlConnection = (HttpURLConnection) urlCASRestTicket.openConnection();
    urlConnection.setRequestMethod("POST");
    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    urlConnection.setDoOutput(true);

    OutputStream os = urlConnection.getOutputStream();
    StringBuilder content = new StringBuilder();
    content.append("service=").append(URLEncoder.encode(serviceName, "UTF-8"));
    os.write(content.toString().getBytes());
    os.close();


    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
    StringBuilder resultSb = new StringBuilder();
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      resultSb.append(inputLine);
    }
    in.close();

    return resultSb.toString();
  }
}
 
