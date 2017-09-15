package sopra.core.util;
/**
* Dépendances
*/
import java.util.Hashtable;
import java.util.Map;

/**
* Permet de convertir un tableau d'octets en chaîne de caractères et
* réciproquement afin de permettant la transmission d'un ensemble de données
* textuelles qui ne sont pas nécessairement des caractères imprimables.
* @author   : SOPRA Group - Arnaud Dessert
* @author   : SOPRA Group
*
*/
public class HexadecimalConverter{
// Constantes

/** Représentation indexée par la valeur des demi-octets donnant la valeur
héxadécimale associée */
private static final char[] HEXADECIMAL_DIGITS = {
                              '0' , '1' , '2' , '3' , '4' , '5' ,
                              '6' , '7' , '8' , '9' , 'A' , 'B' ,
                              'C' , 'D' , 'E' , 'F'
                              };
/** Représentation des valeurs de demi-octets indexée apr leur représentation
textuelle */
private static Map<String, Byte> HEXADECIMAL_STRINGS = null;
//
// Attributs
/** Source qui sera utilisé pour les opérations de conversion (il peut s'agir
soit d'une chaîne de caractères soit d'un tableau d'octets) */
private Object mSource = null;

  /**
  * Constructeur de classe
  */
  static {
    HEXADECIMAL_STRINGS = new Hashtable<String, Byte>();
    HEXADECIMAL_STRINGS.put("0", Byte.valueOf((byte)0));
    HEXADECIMAL_STRINGS.put("1", Byte.valueOf((byte)1));
    HEXADECIMAL_STRINGS.put("2", Byte.valueOf((byte)2));
    HEXADECIMAL_STRINGS.put("3", Byte.valueOf((byte)3));
    HEXADECIMAL_STRINGS.put("4", Byte.valueOf((byte)4));
    HEXADECIMAL_STRINGS.put("5", Byte.valueOf((byte)5));
    HEXADECIMAL_STRINGS.put("6", Byte.valueOf((byte)6));
    HEXADECIMAL_STRINGS.put("7", Byte.valueOf((byte)7));
    HEXADECIMAL_STRINGS.put("8", Byte.valueOf((byte)8));
    HEXADECIMAL_STRINGS.put("9", Byte.valueOf((byte)9));
    HEXADECIMAL_STRINGS.put("A", Byte.valueOf((byte)10));
    HEXADECIMAL_STRINGS.put("B", Byte.valueOf((byte)11));
    HEXADECIMAL_STRINGS.put("C", Byte.valueOf((byte)12));
    HEXADECIMAL_STRINGS.put("D", Byte.valueOf((byte)13));
    HEXADECIMAL_STRINGS.put("E", Byte.valueOf((byte)14));
    HEXADECIMAL_STRINGS.put("F", Byte.valueOf((byte)15));
  }

  /**
  * Retourne la source qui devra être traduite (il s'agit soit d'une chaîne de
  * caractères soit d'un tableau d'octets)
  * @return la source du traitement
  */
  public Object getSource() {
    return mSource;
  }

  /**
  * Fixe la source qui devra être traduite (il s'agit soit d'une chaîne de
  * caractères soit d'un tableau d'octets)
  * @param source source du traitement
  * @exception IllegalArgumentException soulevée si le type de source n'est
  * pas valide
  */
  public void setSource(Object source) {
    //
    if (source != null && !((source instanceof String) || (source instanceof byte[]))) {
      // On ne peut pas traiter ce type de source
      IllegalArgumentException illegalEx = new IllegalArgumentException("Source property should be a String or a Bytes array");
      throw illegalEx;
    }
    //
    mSource = source;
  }

  /**
  * Convertit un tableau d'octets en une chaîne de caractères représentant sa
  * valorisation héxadécimale
  * Les octets sont pris dans l'ordre du tableau et on positionne en sortie tout
  * d'abord le demi-octet de poids fort puis celui de poids faible.
  * @return la représentation héxadécimale
  */
  public String getToHexadecimalString() {
    String result = null;
    //
    byte[] bytes = null;
    //
    Object source = getSource();
    if (source != null && (source instanceof byte[]))
      bytes = (byte[])source;
    //
    if (bytes != null) {
      StringBuffer resultBuffer = new StringBuffer(bytes.length * 2);
      byte currentByte;
      int currentValue;
      //
      for (int i = 0; i < bytes.length; i++) {
        currentByte = bytes[i];
        // On récupère la partie haute de l'octet
        currentValue = (currentByte & 0xF0) >> 4;
        resultBuffer.append(HEXADECIMAL_DIGITS[currentValue]);
        // Puis on positionne la partie basse
        currentValue = (currentByte & 0x0F);
        resultBuffer.append(HEXADECIMAL_DIGITS[currentValue]);
      }
      //
      result = resultBuffer.toString();
    }
    //
    //
    return result;
  }

  /**
  * Construit un tableau d'octets à partir de la représentation textuelle de la
  * source. On suppose que la représentation textuelle donne tout d'abord le
  * demi-octet de poids fort puis le demi-octet de poids faible et ainsi de suite
  * @return le tableau d'octets représenté par la source textuelle
  * @exception IllegalArgumentException soulevée si la représentation textuelle
  * (i.e. source) n'a pas une taille qui est un multiple de 2
  */
  public byte[] getFromHexadecimalString() {
    byte[] result = null;
    //
    String value = null;
    Object source = getSource();
    if (source != null && (source instanceof String))
      value = (String)source;
    //
    if (value != null) {
      if (value.length() % 2 != 0) {
        IllegalArgumentException illegalEx = new IllegalArgumentException("value should contain a number of even characters");
        throw illegalEx;
      }
      result = new byte[value.length() / 2];
      //
      String stringHighValue;
      String stringLowValue;
      byte highValue;
      byte lowValue;
      int numberOfLoops = value.length() / 2;
      int loopCounter = 0;
      int characterIndex = 0;
      //
      while (loopCounter < numberOfLoops) {
        stringHighValue = value.substring(characterIndex, characterIndex + 1);
        highValue = (HEXADECIMAL_STRINGS.get(stringHighValue)).byteValue();
        characterIndex += 1;
        stringLowValue = value.substring(characterIndex, characterIndex + 1);
        lowValue = (HEXADECIMAL_STRINGS.get(stringLowValue)).byteValue();
        characterIndex += 1;
        //
        result[loopCounter] =  (byte)((highValue << 4) | lowValue);
        //
        loopCounter += 1;
      }
    }
    //
    return result;
  }
}

