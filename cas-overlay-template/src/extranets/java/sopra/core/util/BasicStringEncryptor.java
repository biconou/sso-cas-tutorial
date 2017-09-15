/**
* Paquet de définition
*/
package sopra.core.util;
/**
* Dépendances
*/
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
* Permet de coder/décoder une chaîne de caractères à partir d'un algorithme de
* cryptage simple. Le cryptage obtenu n'est pas un cryptage fort mais il permet
* de transmettre des données qui ne sont pas lisibles pour un être humain. On
* pourra utiliser cette classe avec un mécanisme de signature électronique (i.e
* Message Digest) pour garantir un niveau de sécurité élevé.
*
* <p>Pour crypter ou décrypter une chaîne, il convient de positionner
* la <code>source</code> et d'appeler ensuite la propriété adéquate
* <code>makeEncryption</code> ou <code>makeDecryption</code>.</p>
*
* <p>Il est à noter que le codage obtenu est constituer d'un ensemble de caractères
* imprimables.</p>
*
* @author   : SOPRA Group - Arnaud Dessert
* @author   : SOPRA Group - $Author:   msanchez  $
*
*/
public class BasicStringEncryptor{
// Constantes
/** Nombre de caractères aléatoires devant être rajoutés à la source lors de la
phase d'encodage */
public static final int DEFAULT_NUMBER_OF_PADDING_CHARACTERS = 10;
/** Chaîne de caractères utilisée pour entreprendre notre cryptage */
private static final String XOR_CRIPTING_KEY = "La créature de Roswell";
/** Encodage utilisé pour passer d'une chaîne de caractères à des octets et
réciproquement */
private static final String CHARACTER_ENCODING = "ISO-8859-1";
//
// Attributs
/** Source devant être encryptée ou décryptée */
private String mSource = null;
/** Nombre de caractères aléatoires devant être rajoutés à la source lors de
l'opération d'encodage */
private int mNumberOfPaddingCharacters = DEFAULT_NUMBER_OF_PADDING_CHARACTERS;
/** Générateur privé de nombres aléatoires */
private Random mRandomGenerator = null;

  /**
  * Retourne le nombre de caractères aléatoires devant être ajoutés à la source
  * avant l'encodage
  * @return le nombre de caractères aléatoire
  */
  public int getNumberOfPaddingCharacters() {
    return mNumberOfPaddingCharacters;
  }

  /**
  * Fixe le nombre de caractères aléatoires devant être ajoutés à la source
  * avant l'encodage
  * @param number nombre de caractères à employer
  * @exception IllegalArgumentException soulevée si le nombre de caractères est
  * négatif
  */
  public void setNumberOfPaddingCharacters(int number) {
    if (number < 0) {
      IllegalArgumentException illegalEx = new IllegalArgumentException("NumberOfPaddingCharacters property should be a positive number");
      throw illegalEx;
    }
    //
    mNumberOfPaddingCharacters = number;
  }

  /**
  * Retourne la source qui devra être encryptée ou décryptée
  * @return la source du traitement
  */
  public String getSource() {
    return mSource;
  }

  /**
  * Fixe la source qui devra être encryptée ou décryptée
  * @param source source du traitement
  * @exception NullPointerException soulevée si la source est nulle
  */
  public void setSource(String source) {
    if (source == null || source.trim().length() == 0) {
      NullPointerException nullEx = new NullPointerException("Source property cannot be set to null");
      throw nullEx;
    }
    //
    mSource = source;
  }

  /**
  * Retourne une chaîne de caractères correspondant à l'encryption par notre
  * algorithme basique de la source
  * @return la chaîne représentant le résultat encodé ou null si la source est
  * nulle
  * @exception IllegalArgumentException soulevée si la source fait plus de 100
  * caractères
  */
  public String getMakeEncryption() {
    String source = getSource();
    String result = null;
    //
    if (source != null && source.trim().length() != 0) {
    	
	  if (source.length() >= 100) {
		// On ne peut pas traiter des chaînes de plus de 100 caractères car nous
		// disposons uniquement de deux positions pour indiquer la longueur de
		// la source encodée
		IllegalArgumentException illegalEx = new IllegalArgumentException("Source property should have less than 100 characters");
		throw illegalEx;
	  }
        
      StringBuffer encrypted = new StringBuffer();
      //
      int srcLength = source.length();
      // On tire de façon aléatoire la position initiale de la source au sein
      // de la chaîne encodée
      int startSource = _generateRandomNumber(3, getNumberOfPaddingCharacters() + 2);
      // On positionne la position de départ dans les 2 premiers caractères
      String startSourceValue = Integer.toString(startSource);
      if (startSourceValue.length() == 1)
        encrypted.append("0");
      encrypted.append(startSourceValue);
      // On remplit l'espace entre la position et le début de la source avec des
      // caractères aléatoires
      int paddingBefore = startSource - 1 - 2;
      for (int i = 0; i < paddingBefore; i++)
        encrypted.append((char)_generateRandomNumber('A', 'Z'));
      // on ajoute la source
      encrypted.append(source);
      // on regarde s'il convient de rajouter d'autres caractères aléatoires
      // après la source
      int afterPadding = getNumberOfPaddingCharacters() - paddingBefore;
      for (int j = 0; j < afterPadding; j++)
        encrypted.append((char)_generateRandomNumber('A', 'Z'));
      // on ajoute enfin la longueur de la source
      String srcLengthValue = Integer.toString(srcLength);
      if (srcLengthValue.length() == 1)
        encrypted.append("0");
      encrypted.append(srcLengthValue);
      // On applique notre masque XOR
      String xorEncrypted = _applyXOR(encrypted.toString());
      byte[] encryptedBytes = null;
      try {
        encryptedBytes = xorEncrypted.getBytes(CHARACTER_ENCODING);
      }
      catch (UnsupportedEncodingException encodingEx) {
        // Exception soulevée si l'encodage utilisé n'est pas supporté par la JVM
        // Ceci ne devrait jamais se produire puisque l'encodage Latin 1 est
        // supposé être présent sur toutes les implémentations de JVM
        throw new RuntimeException(encodingEx.getMessage());
      }
      // Puis on fournit une représentation imprimable de la chaîne obtenue
      HexadecimalConverter converter = new HexadecimalConverter();
      converter.setSource(encryptedBytes);
      result = converter.getToHexadecimalString();
    }
    //
    return result;
  }

  /**
  * Retourne la chaîne de caractères correspondant à la version encryptée
  * représentée par notre source
  * @return la chaîne ainsi décodée ou null si la source est nulle
  */
  public String getMakeDecryption() {
    String source = getSource();
    String result = null;
    //
    if (source != null && source.trim().length() != 0) {
      // On retrouve les octets correspondant à notre source
      HexadecimalConverter converter = new HexadecimalConverter();
      converter.setSource(source);
      byte[] encryptedBytes = converter.getFromHexadecimalString();
      // On applique le masque XOR
      String encrypted = null;
      try {
        encrypted = new String(encryptedBytes, CHARACTER_ENCODING);
      }
      catch (UnsupportedEncodingException encodingEx) {
        // Exception soulevée si l'encodage utilisé n'est pas supporté par la JVM
        // Ceci ne devrait jamais se produire puisque l'encodage Latin 1 est
        // supposé être présent sur toutes les implémentations de JVM
        throw new RuntimeException(encodingEx.getMessage());
      }
      String decrypted = _applyXOR(encrypted);
      // Puis on extrait la chaîne
      String startDecryptedValue = decrypted.substring(0, 2);
      int startDecrypted = Integer.valueOf(startDecryptedValue).intValue();
      String lengthDecryptedValue = decrypted.substring(decrypted.length() - 2, decrypted.length());
      int lengthDecrypted = Integer.valueOf(lengthDecryptedValue).intValue();
      result = decrypted.substring(startDecrypted - 1, startDecrypted + lengthDecrypted - 1);
    }
    //
    return result;
  }

  /**
  * Fournit un générateur de nombres aléatoires utilisables au sein de cette
  * instance
  * @return le générateur de nombres aléatoires
  */
  private Random _getRandomGenerator() {
    if (mRandomGenerator == null)
      mRandomGenerator = new Random();
    //
    return mRandomGenerator;
  }

  /**
  * Génère un nombre aléatoire dont la valeur est comprise dans l'intervalle
  * [minValue, maxValue]
  * @param minValue borne inférieure
  * @param maxValue borne supérieure
  * @return le nombre aléatoire généré
  */
  private int _generateRandomNumber(int minValue, int maxValue) {
    double result = _getRandomGenerator().nextDouble();
    result = (maxValue - minValue + 1) * result + minValue;
    return (int)result;
  }

  /**
  * Applique un masque de type XOR sur la chaîne de caractères précisée à l'aide
  * de notre masque de cryptage (c.f. XOR_CRYPTING_KEY). (remarque : l'opération
  * binaire XOR permet si on applique deux fois la même opération de retrouver
  * la source de la première opération)
  * @param toWhat source sur laquelle le masque sera appliqué
  * @return le résultat obtenue par l'application du masque. Attention les
  * caractères obtenus ne sont pas nécessairement des caractères imprimables.
  */
  private String _applyXOR(String toWhat) {
    try {
      // On récupère les octets constituant la clé
      byte[] keyBytes = XOR_CRIPTING_KEY.getBytes(CHARACTER_ENCODING);
      int lengthKey = keyBytes.length;
      int xorIndex = 0;
      // On fait de même avec la chaîne à encoder
      byte[] toWhatBytes = toWhat.getBytes(CHARACTER_ENCODING);
      int lengthToWhat = toWhatBytes.length;
      //
      byte[] resultBytes = new byte[lengthToWhat];
      //
      // On applique le masque XOR sur la source. Attention les octets résultants
      // ne réprésentent pas forcément des caractères imprimables
      for (int i = 0; i < lengthToWhat; i++) {
        resultBytes[i] = (byte)(toWhatBytes[i] ^ keyBytes[xorIndex]);
        xorIndex = ((xorIndex + 1) == lengthKey ? 0 : xorIndex + 1);
      }
      //
      return new String(resultBytes, CHARACTER_ENCODING);
    }
    catch (UnsupportedEncodingException encodingEx) {
      // Exception soulevée si l'encodage utilisé n'est pas supporté par la JVM
      // Ceci ne devrait jamais se produire puisque l'encodage Latin 1 est
      // supposé être présent sur toutes les implémentations de JVM
      throw new RuntimeException(encodingEx.getMessage());
    }
  }
}
