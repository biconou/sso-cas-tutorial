/**
 * Paquet de définition
 */
package sopra.core.util;
/**
 * Dépendances
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Permet de sécuriser le passage d'arguments entre deux machines via HTTP.
 * <p>
 * Cet utilitaire peut être utilisé de deux manières :
 * <UL>
 * <LI>soit pour encoder un ensemble de paramètres HTTP</LI>
 * <LI>soit pour vérifier une requête</LI>
 * </UL>
 * <p>
 * <b>Restriction :</b>
 * <UL>
 * <LI>Ne gère <b>pas</b> les paramètres multivalués !</LI>
 * <LI>Les paramètres rp*  sont réservés où * représentent un caractère numérique.</LI>
 * </UL>
 * <p>
 * La vérification se gère à deux niveaux :
 * <UL>
 * <LI>Il ne doit pas s'être écoulé un intervalle trop long entre la génération
 * de la requête et sa vérification.</LI>
 * <LI>La signature associée à la requête doit être vérifiée</LI>
 * </UL>
 * <p>
 * <p>Utilisation typique en Signature :
 * <pre>
 *   HttpServletRequestParametersDigest digester = new HttpServletRequestParametersDigest(password);
 *   //
 *   // Construction des paramètres de la requête
 *   Map parameters = new HashMap();
 *   parameters.put("login", "monLoginUtilisateur");
 *   parameters.put("task", "openMyWorkspace");
 *
 *   String parametersURL = oTmpDigest.signRequestParameters(parameters);
 *   String redirectURL = "http://www.serveur.com/nom_de_servlet?" + parametersURL;
 *   request.sendRedirect(redirectURL);
 * </pre></p>
 *
 * <p>Utilisation typique en Vérification :
 * <pre>
 *    HttpServletRequestParametersDigest digester = new HttpServletRequestParametersDigest(password);
 *    if (digester.verifyRequestParameters(parameters)) {
 *        // OK
 *        ...
 *    } else
 *       throw new ServletException("Request is not trusted");
 * </pre></p>
 *
 * @author : SOPRA Group - Arnaud Dessert
 * @author : SOPRA Group
 */
public class HttpServletRequestParametersDigest {
// Constantes

  private static final Logger log = LoggerFactory.getLogger(HttpServletRequestParametersDigest.class);


  /** Mot de passe utilisé par défaut pour singulariser la signature */
  public static final String DEFAULT_DIGEST_STARTING_KEY = "sopra.servlet.util.HttpServletRequestParametersDigest";
  /** Algorithme interne utilisé pour créer la signature (i.e. MD5) */
  public static final String DEFAULT_DIGEST_ALGORITHM = "MD5";
  /**
   * Nombre de millisecondes permettant d'indiquer que la signature est toujours
   * valide 5 * 60 * 1000 =  300 000 ms soit 5 minutes
   */
  public static final long DEFAULT_DIGEST_VALIDITY = 5 * 60 * 1000;
  /**
   * Encodage utilisé pour passer d'une chaîne de caractères à des octets et
   * réciproquement (i.e. <code>UTF-8</code>)
   */
  private static final String _CHARACTERS_ENCODING = "UTF-8";
  /**
   * Encodage utilisé pour défaut pour construire les différents paramètres de l'URL
   * (i.e. <code>UTF-8</code>)
   *
   * @since Kernel 6.0
   */
  private static final String _URL_ENCODING = "UTF-8";
  /**
   * Paramètre au sein de la requête qui représente la date d'emission de la
   * signature (i.e. rp1)
   */
  public static final String DATE_REQUEST_PARAMETER_NAME = "rp1";
  /**
   * Paramètre au sein de la requête qui représente la signature des différents
   * paramètres (i.e. rp2)
   */
  public static final String SIGNATURE_REQUEST_PARAMETER_NAME = "rp2";
  /** Base permettant d'exprimer la date de signature (base 36 par défaut) */
  private static final int RADIX_ENCODING = 36;
  /** Séparateur standard entre les différents paramètres d'une requête HTTP (i.e. &) */
  private static final String REQUEST_PARAMETER_SEPARATOR = "&";
  /** Séparateur standard entre le nom et la valeur d'un paramètre d'une requête HTTP (i.e. =) */
  private static final String REQUEST_PARAMETER_NAME_VALUE_SEPARATOR = "=";
//
// Attributs
  /** Clé utilisée pour singulariser la signature */
  private String mDigestStartingKey = DEFAULT_DIGEST_STARTING_KEY;
  /**
   * Nombre de millisecondes permettant d'indiquer que la signature est toujours
   * valide
   */
  private long mDigestValidity = DEFAULT_DIGEST_VALIDITY;

  /**
   * Construit un nouveau gestionnaire de signature des paramètres d'une requête
   * HTTP avec une clé de singularisation et une durée de validité par défaut.
   */
  public HttpServletRequestParametersDigest() {
    this(DEFAULT_DIGEST_STARTING_KEY, DEFAULT_DIGEST_VALIDITY);
  }

  /**
   * Construit un nouveau gestionnaire de signature des paramètres d'une requête
   * HTTP avec une durée de validité par défaut.
   *
   * @param startingDigestKey clé permettant de singulariser la signature
   */
  public HttpServletRequestParametersDigest(String startingDigestKey) {
    this(startingDigestKey, DEFAULT_DIGEST_VALIDITY);
  }

  /**
   * Construit un nouveau gestionnaire de signature des paramètres d'une requête
   * HTTP.
   *
   * @param startingDigestKey clé permettant de singulariser la signature
   * @param digestValidity    durée de validité de la signature exprimée en millisecondes
   */
  public HttpServletRequestParametersDigest(String startingDigestKey, long digestValidity) {
    setDigestStartingKey(startingDigestKey);
    setDigestValidity(digestValidity);
  }

  /**
   * Fixe la clé permettant de singulariser la signature
   *
   * @param startingKey clé permettant de singulariser l'algorithme
   * @throws NullPointerException soulevée si la clé est nulle
   */
  protected void setDigestStartingKey(String startingKey) {
    if (startingKey == null || startingKey.trim().length() == 0) {
      NullPointerException nullEx = new NullPointerException("DigestStartingKey property cannot be set to null");
      log.error("", nullEx);
      throw nullEx;
    }
    //
    log.debug("setDigestStartingKey() <-- {}", startingKey);
    //
    mDigestStartingKey = startingKey;
  }

  /**
   * Retourne la clé permettant de singulariser la signature
   *
   * @return la clé permettant de singulariser la signature numérique
   */
  public String getDigestStartingKey() {
    return mDigestStartingKey;
  }

  /**
   * Fixe le nombre de millisecondes pouvant s'écouler entre l'émission de la
   * signature et son contrôle pour considérer que cette dernière est valide
   *
   * @param numberOfMs nombre de millisecondes avant l'expiration de la signature
   * @throws IllegalArgumentException soulevée si le nombre de millisecondes
   *                                  est négatif
   */
  protected void setDigestValidity(long numberOfMs) {
    if (numberOfMs <= 0) {
      IllegalArgumentException illegalEx = new IllegalArgumentException("DigestValidity property should be a positive number");
      log.error("", illegalEx);
      throw illegalEx;
    }
    //
    log.debug("setDigestValidity() <-- {}", numberOfMs);
    //
    mDigestValidity = numberOfMs;
  }

  /**
   * Retourne le nombre de millisecondes pouvant s'écouler entre l'émission de la
   * signature et son contrôle pour considérer que cette dernière est valide
   *
   * @return le nombre de millisecondes avant de considérer la signature comme
   * expirée
   */
  public long getDigestValidity() {
    return mDigestValidity;
  }

  /**
   * Signe le tableau d'octets précisé
   *
   * @param bytes tableau d'octets dont on veut connaître la signature
   * @return la représentation textuelle de la signature (en réalité il s'agit
   * de la représentation textuelle et hexadécimale des octets constituant la
   * signature)
   */
  protected String sign(byte[] bytes) {
    String signature = null;
    //
    MessageDigest digester = _createMessageDigest(DEFAULT_DIGEST_ALGORITHM,
      getDigestStartingKey());
    if (digester != null) {
      digester.update(bytes);
      byte[] signatureBytes = digester.digest();
      HexadecimalConverter converter = new HexadecimalConverter();
      converter.setSource(signatureBytes);
      signature = converter.getToHexadecimalString();
    }
    //
    return signature;
  }

  /**
   * Se charge de signer l'ensemble des paramètres de la requête. Une fois
   * l'opération achevée, on retourne une expression représentant les différents
   * paramètres selon le format HTTP d'une méthode de type GET; c'est à dire que
   * l'on trouve une expression du type parameter1=value1&amp;parameter2=value2&amp;...
   *
   * @param parameterValues liste des valeurs des différents paramètres indexées
   *                        par le nom du paramètre
   * @return un chaîne de caractères représentant les différents paramètres
   * correctement encodés et signés
   * @throws IllegalArgumentException soulevée si la liste des paramètres
   *                                  contient un des deux noms de paramètres réservés pour la date d'émission de
   *                                  la signature et la signature
   */
  public String signRequestParameters(Map<String, String> parameterValues) {
    if (parameterValues.containsKey(DATE_REQUEST_PARAMETER_NAME)) {
      IllegalArgumentException illegalEx = new IllegalArgumentException(DATE_REQUEST_PARAMETER_NAME + " is a reserved parameter'name");
      log.error("", illegalEx);
      throw illegalEx;
    }
    //
    if (parameterValues.containsKey(SIGNATURE_REQUEST_PARAMETER_NAME)) {
      IllegalArgumentException illegalEx = new IllegalArgumentException(SIGNATURE_REQUEST_PARAMETER_NAME + " is a reserved parameter'name");
      log.error("", illegalEx);
      throw illegalEx;
    }
    //
    String signedRequestParameters = null;
    //
    // On ordonne les différents paramètres afin d'obtenir les mêmes signatures
    // pour une même liste de paramètres
    SortedMap<String, String> sortedParameterValues = new TreeMap<String, String>(parameterValues);
    //
    // On ajoute la date d'emission de la signature
    String signatureDateValue = Long.toString(System.currentTimeMillis(), RADIX_ENCODING);
    sortedParameterValues.put(DATE_REQUEST_PARAMETER_NAME, signatureDateValue);
    //
    // On forme ensuite la liste des différents paramètres
    String encodedParameterValues = encodeURLParameters(sortedParameterValues);
    //
    try {
      // On signe l'ensemble
      String digest = sign(encodedParameterValues.getBytes(_CHARACTERS_ENCODING));
      //
      // Puis on rajoute la signature à notre bout d'URL
      StringBuffer completedURL = new StringBuffer(encodedParameterValues);
      completedURL.append(REQUEST_PARAMETER_SEPARATOR);
      completedURL.append(SIGNATURE_REQUEST_PARAMETER_NAME);
      completedURL.append(REQUEST_PARAMETER_NAME_VALUE_SEPARATOR);
      completedURL.append(digest);
      //
      signedRequestParameters = completedURL.toString();
    }
    catch (UnsupportedEncodingException encodingEx) {
      // Exception soulevée si l'encodage utilisé n'est pas supporté par la JVM
      // Ceci ne devrait jamais se produire puisque l'encodage Latin 1 est
      // supposé être présent sur toutes les implémentations de JVM
      log.error("", encodingEx);
      throw new RuntimeException(encodingEx.getMessage());
    }
    //
    return signedRequestParameters;
  }

  /**
   * Vérifie que les paramètres de la requêtes non pas été altérés et que la
   * signature n'est pas périmée
   *
   * @param parameterValues liste des valeurs des paramètres indexée par leurs noms
   * @return true si la requête est considérée comme valide, false dans le
   * cas contraire
   */
  public boolean verifyRequestParameters(Map<String, String> parameterValues) {
    boolean isOK = false;
    //
    log.debug("verifyRequestParameters(Map)");
    //
    // On contrôle que la signature est présente
    if (!parameterValues.containsKey(SIGNATURE_REQUEST_PARAMETER_NAME)) {
      log.debug("{} parameter cannot be found. Request's parameters are not valid", SIGNATURE_REQUEST_PARAMETER_NAME);
    }
    else {
      // On contrôle que la date de signature est présente
      if (!parameterValues.containsKey(DATE_REQUEST_PARAMETER_NAME)) {
        log.debug("{} parameter cannot be found. Request's parameters are not valid", DATE_REQUEST_PARAMETER_NAME);
      }
      else {
        log.debug("check time stamp");
        // On contrôle alors que la signature n'est pas périmée
        String signatureDateValue = parameterValues.get(DATE_REQUEST_PARAMETER_NAME);
        long signatureDate = Long.parseLong(signatureDateValue, RADIX_ENCODING);
        long nowDate = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
          Date signatureDateDebug = new Date(signatureDate);
          log.debug("signature date : " + signatureDateDebug);
          Date nowDateDebug = new Date(nowDate);
          log.debug("now date :" + nowDateDebug);
        }
        if (java.lang.Math.abs(nowDate - signatureDate) > getDigestValidity()) {
          // La signature est périmée
          log.debug("Request's parameters are out of date");
        }
        else {
          log.debug("check MD5 sum");
          // On contrôle enfin que la signature n'a pas été altérée
          SortedMap<String, String> parameterValuesWithoutDigest = new TreeMap<String, String>(parameterValues);
          String currentSignature = parameterValuesWithoutDigest.remove(SIGNATURE_REQUEST_PARAMETER_NAME);
          String encodedParameterValues = encodeURLParameters(parameterValuesWithoutDigest);
          try {
            String checkSignature = sign(encodedParameterValues.getBytes(_CHARACTERS_ENCODING));
            if (currentSignature.equals(checkSignature)) {
              log.debug("Request's parameters are valid");
              isOK = true;
            }
            else {
              log.debug("Signature is not valid !");
            }
          }
          catch (UnsupportedEncodingException encodingEx) {
            // Exception soulevée si l'encodage utilisé n'est pas supporté par la JVM
            // Ceci ne devrait jamais se produire puisque l'encodage Latin 1 est
            // supposé être présent sur toutes les implémentations de JVM
            log.error("", encodingEx);
            throw new RuntimeException(encodingEx.getMessage());
          }
        } // if (java.lang.Math.abs(nowDate - signatureDate) > getDigestValidity()) {
      } // if (!parameterValues.containsKey(DATE_REQUEST_PARAMETER_NAME)) {
    } // if (!parameterValues.containsKey(SIGNATURE_REQUEST_PARAMETER_NAME)) {
    //
    return isOK;
  }

  /**
   * Forme une chaîne de caractères représentant les différents paramètres précisés
   *
   * @param parameterValues liste ordonnée des valeurs des paramètres indexée par
   *                        leurs noms
   * @return une chaîne correctement encodée représentant les différents
   * paramètres
   */
  protected String encodeURLParameters(SortedMap<String, String> parameterValues) {
    StringBuilder result = new StringBuilder();
    boolean isFirst = true;
    //
    String parameterName = null;
    String parameterValue = null;
    //
    Iterator<Map.Entry<String, String>> parameters = parameterValues.entrySet().iterator();
    Map.Entry<String, String> currentParameter = null;
    while (parameters.hasNext()) {
      currentParameter = parameters.next();
      //
      if (isFirst) {
        isFirst = false;
      }
      else {
        result.append(REQUEST_PARAMETER_SEPARATOR);
      }
      //
      parameterName = currentParameter.getKey();
      parameterValue = currentParameter.getValue();
      //
      try {
        result.append(URLEncoder.encode(parameterName, _URL_ENCODING));
        result.append(REQUEST_PARAMETER_NAME_VALUE_SEPARATOR);
        if (parameterValue != null) {
          result.append(URLEncoder.encode(parameterValue, _URL_ENCODING));
        }
      }
      catch (UnsupportedEncodingException encodingEx) {
        // Exception soulevée si l'encodage utilisé n'est pas supporté par la JVM
        // Ceci ne devrait jamais se produire puisque l'encodage UTF-8 est
        // supposé être présent sur toutes les implémentations de JVM
        log.error("", encodingEx);
        throw new RuntimeException(encodingEx.getMessage());
      }
    } // while (parameters.hasNext()) {
    //
    return result.toString();
  }

  /**
   * Crée un gestionnaire capable de signer un tableau d'octets
   *
   * @param algorithm   algorithme employé au sein du fournisseur
   * @param startingKey clé permettant de singulariser la signature (i.e. préfixe
   *                    ajouté au début des octets à signer)
   * @return le gestionnaire de signature ou null si ce dernier n'a pu être
   * créé
   */
  private MessageDigest _createMessageDigest(String algorithm, String startingKey) {
    MessageDigest digester = null;
    //
    try {
      digester = MessageDigest.getInstance(algorithm);
      digester.update(startingKey.getBytes(_CHARACTERS_ENCODING));
    }
    catch (NoSuchAlgorithmException algorithmEx) {
      // Exception soulevée si l'algorithme n'est pas supporté. Ne devrait jamais
      // se produire
      log.error("", algorithmEx);
    }
    catch (UnsupportedEncodingException encodingEx) {
      // Exception soulevée si l'encodage utilisé n'est pas supporté par la JVM
      // Ceci ne devrait jamais se produire puisque l'encodage UTF-8 est
      // supposé être présent sur toutes les implémentations de JVM
      log.error("", encodingEx);
    }
    //
    return digester;
  }
}
