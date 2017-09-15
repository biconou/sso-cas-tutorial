package sopra.immobilier.ws.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import sopra.core.util.BasicStringEncryptor;
import sopra.core.util.HttpServletRequestParametersDigest;


/**
 * Classe de base d'un client d'authentification via Web Service.
 * <p>
 * <p>Cette classe et ses filles doivent suivre le contrat <code>WSAuthenticator</code>.</p>
 * <p>
 * <p>Les paramétres suivants sont obligatoires pour gérer une authentification via Web Service :
 * <ul>
 * <li><code>UserID</code> : Nom d'utilisateur é identifier</li>
 * <li><code>UserPWD</code> : Mot de passe de l'utilisateur é identifier</li>
 * <li><code>URLStart</code> : Base de l'URL d'appel du Web Service d'authentification</li>
 * </ul>
 * </p>
 * <p>
 * <p>Aprés l'initialisation des paramétres obligatoires de cet objet, l'authentification de l'utilisateur
 * se fait par l'appel é la méthode publique <code>{@link #authenticate()}</code>.<br />
 * Cette méthode se charge de construire l'URL d'appel avec les paramétres encryptés puis de se connecter é celle ci
 * pour obtenir la réponse du Web Service.<br />
 * Elle retourne un objet <code>{@link sopra.immobilier.ws.core.WSAuthenticationMessage}</code> comprennant le résultat de l'authentification.</p>
 * <p>
 * <p>Il est possible de surcharger la plupart des méthodes dans les classes filles afin d'adapter le fonctionnement
 * du client d'authentification aux caractéristiques de Web Service qui se trouve en face de lui.<br />
 * Les méthodes é surcharger sont :<br />
 * <ul>
 * <li><code>{@link #computeWSAuthenticationURLParameters()}</code> : Construit la table de correspondance des paramétres de l'URL</code>
 * <li><code>{@link #computeWSAuthenticationURL()}</code> : Construit l'URL d'appel au Web Service d'authentification</code>
 * <li><code>{@link #openWSAuthenticationURLConnection(String)}</code> : Ouvre la connexion vers l'URL donnée</code>
 * <li><code>{@link #readWSAuthenticationURLResult(InputStream)}</code> : Lit et interpréte la réponse du Web Service</code>
 * <li><code>{@link #manageUnknownException(Exception)}</code> : Gére les erreurs spécifiques é un Web Service particulier</code>
 * <li><code>{@link #logWSAuthenticationError(String, Exception)}</code> : Journalise les erreurs d'authentification</code>
 * </ul>
 *
 * @author : SOPRA Group - Author:   fAgier
 * @author : SOPRA Group - $Author:   jburdeyron  $
 * @version : $Revision:   1.2  $
 */
public class WSAuthenticationClient implements WSAuthenticator {
  // Constantes
  /**
   * Durée maximale de validité de la requéte HTTP en vue de la signature
   * automatique (i.e. une minute)
   */
  public static final long DEFAULT_DIGEST_VALIDITY = 60000;

  // Membres
  /** Objet de journalisation des messages liés é la classe */
  protected Logger log = LoggerFactory.getLogger(this.getClass());

  /** Identifiant de l'utilisateur é authentifier */
  private String mUserID = null;
  /** Mot de passe de l'utilisateur é authentifier */
  private String mUserPWD = null;
  /** Base de l'URL du Web Service qui va authentifier l'utilisateur */
  private String mURLStart = null;
  /** Durée de validité de la requéte d'authentification */
  private long mDigestValidity = DEFAULT_DIGEST_VALIDITY;

  /** Constructeur par défaut, rien é faire. */
  public WSAuthenticationClient() {
  }

  /**
   * Se charge d'authentifier l'utilisateur donné sur un service web donné.
   *
   * @return Un message précisant l'authentification ou la non authentification de l'utilisateur ou encore une erreur technique
   */
  public final WSAuthenticationMessage authenticate() {

    if (StringUtils.isBlank(getUserID())) {
      throw new IllegalArgumentException("userID cannot be null");
    }
    if (StringUtils.isBlank(getUserPWD())) {
      throw new IllegalArgumentException("userPWD cannot be null");
    }
    if (StringUtils.isBlank(getURLStart())) {
      throw new IllegalArgumentException("URLStart cannot be null");
    }

    return callWSAuthenticationURL(computeWSAuthenticationURL());
  }

  /**
   * Se charge de construire la table de correspondance des paramétres de la requéte
   * HTTP pour l'authentification via Web Service.
   *
   * @return La table de correspondance des paramétres
   */
  protected Map<String, String> computeWSAuthenticationURLParameters() {
    BasicStringEncryptor encryptor = new BasicStringEncryptor();
    encryptor.setSource(getUserID());
    String encryptedUserID = encryptor.getMakeEncryption();
    encryptor.setSource(getUserPWD());
    String encryptedUserPWD = encryptor.getMakeEncryption();

    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("userID", encryptedUserID);
    parameters.put("userPWD", encryptedUserPWD);

    return parameters;
  }

  /**
   * Se charge de construire l'URL d'appel du Web Service d'authentification.
   * <p>
   * <p>Cette méthode s'occupe de récupérer la base de l'URL d'appel puis elle lui
   * concaténe les paramétres d'authentification formatté.</p>
   *
   * @return L'URL d'appel du Web Service d'authentification
   */
  protected String computeWSAuthenticationURL() {
    HttpServletRequestParametersDigest digester = new HttpServletRequestParametersDigest(
      HttpServletRequestParametersDigest.DEFAULT_DIGEST_STARTING_KEY,
      getDigestValidity()
    );

    Map<String, String> parameters = computeWSAuthenticationURLParameters();

    return getURLStart() + digester.signRequestParameters(parameters);
  }

  /**
   * Se charge de contacter l'URL donnée pour obtenir la réponse d'authentification
   * de l'utilisateur.
   *
   * @param pWSAuthenticationURLString L'URL d'authentification
   * @return Le message de retour d'authentification
   */
  protected final WSAuthenticationMessage callWSAuthenticationURL(String pWSAuthenticationURLString) {
    log.debug("Attempting connection to : " + pWSAuthenticationURLString + " [" + getClass().getCanonicalName() + "]");
    // Réponse du Web Service
    WSAuthenticationMessage authenticationResponse = null;
    // Connexion é l'URL donnée
    URLConnection tWSAuthenticationConnection = null;
    try {
      // Ouverture de la connexion
      tWSAuthenticationConnection = openWSAuthenticationURLConnection(pWSAuthenticationURLString);

      // Paramétrage des durées de Time Out sur la connexion (Connection Timeout + Read Timeout)
      tWSAuthenticationConnection.setConnectTimeout(15000);
      tWSAuthenticationConnection.setReadTimeout(20000);

      // Si la connexion est établie sans erreur
      if (tWSAuthenticationConnection != null) {
        // Récupération de la réponse du Web Service d'authentification
        authenticationResponse = new WSAuthenticationMessage(readWSAuthenticationURLResult(tWSAuthenticationConnection.getInputStream()));
      }
    }
    catch (MalformedURLException malformedURLEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Malformed URL", malformedURLEx);
    }
    catch (SocketException socketEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Connection denied or reset", socketEx);
    }
    catch (SocketTimeoutException timeoutEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Connection timeout", timeoutEx);
    }
    catch (UnknownHostException unknownHostEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Unknown host", unknownHostEx);
    }
    catch (IOException ioEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Connection error", ioEx);
    }
    catch (Exception unknownEx) {
      // Délégation de la gestion des exceptions inconnues
      authenticationResponse = manageUnknownException(unknownEx);
    }

    logWSAuthenticationMessage(authenticationResponse);
    return authenticationResponse;
  }

  /**
   * Se charge d'ouvrir la connexion vers l'URL donnée.
   *
   * @param pWSAuthenticationURLString L'URL d'authentification
   * @return La connexion vers le Web Service d'authentification
   * @throws IOException Soulevée si un probléme survient lors de l'ouverture de la connexion
   */
  protected URLConnection openWSAuthenticationURLConnection(String pWSAuthenticationURLString) throws IOException {
    // Vérification de l'URL fournie
    URL tWSAuthenticationURL = new URL(pWSAuthenticationURLString);

    // Ouverture de la connexion
    return tWSAuthenticationURL.openConnection();
  }

  /**
   * Se charge de lire la réponse du Web Service dans le flux HTTP.
   *
   * @param pWSAuthenticationURLStream Le flux é lire
   * @return <code>true</code> si l'authentification est un succés, <code>false</code> sinon
   * @throws Exception Cette méthode peut soulever les exceptions de type <code>JDOMException</code> ou <code>IOException</code>
   *                   cependant, on laisse le type de base pour une généricité accrue. Il conviendra de gérer les exceptions spécifiques
   *                   en surchargeant la méthode {@link #manageUnknownException(Exception)}.
   */
  protected boolean readWSAuthenticationURLResult(InputStream pWSAuthenticationURLStream) throws Exception {
    // Récupération de la réponse du Web Service d'authentification
    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document parsedResponse = documentBuilder.parse(pWSAuthenticationURLStream);

    String content = parsedResponse.getDocumentElement().getTextContent();
    return Boolean.parseBoolean(content);
  }


  /**
   * Se charge de gérer les exceptions non prévues par le cas standard.
   *
   * @param unknownEx L'exception inconnue
   * @return Le message correspondant
   */
  protected WSAuthenticationMessage manageUnknownException(Exception unknownEx) {
    return computeWSAuthenticationErrorMessage("Unknown error", unknownEx);
  }

  /**
   * Se charge de construire le message d'authentification en cas d'erreur, de journaliser l'exception
   * associée et de renseigner un message vulgarisé.
   *
   * @param pPopularizedMessage Le message vulgarisé associé é l'exception
   * @param cause               La cause de l'erreur
   * @return Le message d'authentification
   */
  private WSAuthenticationMessage computeWSAuthenticationErrorMessage(String pPopularizedMessage, Exception cause) {
    logWSAuthenticationError(pPopularizedMessage, cause);

    return new WSAuthenticationMessage(false, pPopularizedMessage);
  }

  /**
   * Se charge de journaliser l'erreur survenue.
   *
   * @param pPopularizedMessage Un message vulgarisé
   * @param cause               L'exception cause de l'erreur
   */
  protected final void logWSAuthenticationError(String pPopularizedMessage, Exception cause) {
    log.error("Authentication error : " + pPopularizedMessage + " [" + cause.getMessage() + "]");
  }

  /**
   * Se charge de journaliser un message d'authentification donné.
   *
   * @param pWSAuthenticationMessage Le message d'authentification é journaliser
   */
  protected final void logWSAuthenticationMessage(WSAuthenticationMessage pWSAuthenticationMessage) {
    if (pWSAuthenticationMessage != null) {
      if (pWSAuthenticationMessage.getStatut().booleanValue()) { // Authentifié
        log.info("Authentication success (" + getUserID() + "): " + pWSAuthenticationMessage.getStatut() + " [" + pWSAuthenticationMessage.getMessage() + "]");
      }
      else { // Non authentifié ou erreur
        log.error("Authentication failed (" + getUserID() + "): " + pWSAuthenticationMessage.getStatut() + " [" + pWSAuthenticationMessage.getMessage() + "]");
      }
    }
  }

  /**
   * Se charge de construire la chaine de paramétres pour une requéte HTTP.
   *
   * @param data Les données é positionner en paramétres
   * @return La chaine de paramétres
   */
  protected String toQueryString(Map<String, String> data) {
    StringBuffer queryString = new StringBuffer();

    for (Entry<?, ?> pair : data.entrySet()) {
      queryString.append(pair.getKey() + "=");
      queryString.append(pair.getValue() + "&");
    }

    if (queryString.length() > 0) {
      queryString.deleteCharAt(queryString.length() - 1);
    }

    return queryString.toString();
  }

  /**
   * @return L'identifiant de l'utilisateur é authentifier
   */
  public String getUserID() {
    return mUserID;
  }

  /**
   * Permet de positionner l'identifiant de l'utilisateur é authentifier
   *
   * @param value L'identifiant de l'utilisateur é authentifier
   */
  public void setUserID(String value) {
    mUserID = value;
  }

  /**
   * @return Le mot de passe de l'utilisateur é authentifier
   */
  public String getUserPWD() {
    return mUserPWD;
  }

  /**
   * Permet de positionner le mot de passe de l'utilisateur é authentifier
   *
   * @param value Le mot de passe de l'utilisateur é authentifier
   */
  public void setUserPWD(String value) {
    mUserPWD = value;
  }

  /**
   * @return La base de l'URL du Web Service d'authentification
   */
  public String getURLStart() {
    return mURLStart;
  }

  /**
   * Permet de positionner la base de l'URL du Web Service d'authentification
   *
   * @param value La base de l'URL du Web Service d'authentification
   */
  public void setURLStart(String value) {
    mURLStart = value;
  }

  /**
   * @return La durée de validité de la requéte d'authentification
   */
  private long getDigestValidity() {
    return mDigestValidity;
  }

  /**
   * Permet de positionner la durée de validité de la requéte d'authentification
   *
   * @param value La durée de validité de la requéte d'authentification
   */
  private void setDigestValidity(long value) {
    mDigestValidity = value;
  }

  /**
   * @return La durée de validité de la requéte d'authentification
   */
  public int getValidity() {
    return (int) (getDigestValidity() / (60 * 1000));
  }

  /**
   * Permet de positionner la durée de validité de la requéte d'authentification
   *
   * @param value La durée de validité de la requéte d'authentification
   */
  public void setValidity(int value) {
    setDigestValidity(value * (60 * 1000));
  }


  /**
   * Se charge de reccuperer les informations de l'utilisateur donné.
   *
   * @return un tableau de string avec tous les informations
   */
  public Object[] authenticateInformation(String URL) {
    return callWSInformationUserURL(URL);
  }


  /**
   * Se charge de contacter l'URL donnée pour obtenir les infos
   * de l'utilisateur.
   *
   * @param pWSAuthenticationURLString L'URL d'authentification
   * @return Le message de retour d'authentification
   */
  protected Object[] callWSInformationUserURL(String pWSAuthenticationURLString) {
    log.debug("Attempting connection to : " + pWSAuthenticationURLString + " [" + getClass().getCanonicalName() + "]");

    // Réponse du Web Service
    Object infos[] = null;
    WSAuthenticationMessage authenticationResponse = null;

    // Connexion é l'URL donnée
    URLConnection tWSInformationConnection = null;
    try {
      // Ouverture de la connexion
      tWSInformationConnection = openWSAuthenticationURLConnection(pWSAuthenticationURLString);

      // Paramétrage des durées de Time Out sur la connexion (Connection Timeout + Read Timeout)
      tWSInformationConnection.setConnectTimeout(15000);
      tWSInformationConnection.setReadTimeout(20000);

      // Si la connexion est établie sans erreur
      if (tWSInformationConnection != null) {
        // Récupération de la réponse du Web Service d'authentification
        infos = readWSInformationURLResult(tWSInformationConnection.getInputStream());

      }
    }
    catch (MalformedURLException malformedURLEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Malformed URL", malformedURLEx);
      logWSAuthenticationMessage(authenticationResponse);
    }
    catch (SocketException socketEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Connection denied or reset", socketEx);
      logWSAuthenticationMessage(authenticationResponse);
    }
    catch (SocketTimeoutException timeoutEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Connection timeout", timeoutEx);
      logWSAuthenticationMessage(authenticationResponse);
    }
    catch (UnknownHostException unknownHostEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Unknown host", unknownHostEx);
      logWSAuthenticationMessage(authenticationResponse);
    }
    catch (IOException ioEx) {
      authenticationResponse = computeWSAuthenticationErrorMessage("Connection error", ioEx);
      logWSAuthenticationMessage(authenticationResponse);
    }
    catch (Exception unknownEx) {
      // Délégation de la gestion des exceptions inconnues
      authenticationResponse = manageUnknownException(unknownEx);
      logWSAuthenticationMessage(authenticationResponse);
    }

    return infos;
  }

  /**
   * Se charge de lire la réponse du Web Service dans le flux HTTP.
   *
   * @param pWSInformationURLStream Le flux à lire
   */
  protected Object[] readWSInformationURLResult(InputStream pWSInformationURLStream) throws Exception {
    // Récupération de la réponse du Web Service d'authentification
    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document parsedResponse = documentBuilder.parse(pWSInformationURLStream);

    String content = parsedResponse.getDocumentElement().getTextContent();

    // TODO, je sais pas trop ce qu'il faut faire ici.
    return null;
  }
}