package sopra.immobilier.ws.core;

/**
 * Contrat que doit suivre chaque classe prétendant vouloir gérer l'authentification d'un utilisateur via un Web Service.
 * 
 * <TABLE>
 *   <TR>
 *     <TD><B>Attributs PVCS :</B></TD>
 *     <TD>&nbsp;</TD>
 *   </TR>
 *   <TR>
 *     <TD><I>Nom du fichier :</I></TD>
 *     <TD>$Archive:   O:/XNET/XREF/archives/cas/WEB-INF/classes/sopra/immobilier/ws/core/WSAuthenticator.java-arc  $</TD>
 *   </TR>
 *   <TR>
 *     <TD><I>Modifié le :</I></TD>
 *     <TD>$Date:   Feb 08 2013 09:31:38  $</TD>
 *   </TR>
 * </TABLE>
 *
 * @version  : $Revision:   1.0  $
 * @author   : SOPRA Group - Author:   fAgier
 * @author   : SOPRA Group - $Author:   fAgier  $
 */
public interface WSAuthenticator {
	// Constantes
	/** Version de la classe extraite du gestionnaire de configuration */
	public static final String CLASS_VERSION = "$Revision:   1.0  $";

	/**
	 * Se charge d'authentifier l'utilisateur donné sur un service web donné.
	 * 
	 * @return Un message précisant l'authentification ou la non authentification de l'utilisateur ou encore une erreur technique
	 */
	public WSAuthenticationMessage authenticate();

	/**
	 * Permet de positionner l'identifiant de l'utilisateur à authentifier
	 * @param value L'identifiant de l'utilisateur à authentifier
	 */
	public void setUserID(String value);

	/**
	 * Permet de positionner le mot de passe de l'utilisateur à authentifier
	 * @param value Le mot de passe de l'utilisateur à authentifier
	 */
	public void setUserPWD(String value);

	/**
	 * Permet de positionner la base de l'URL du Web Service d'authentification
	 * @param value La base de l'URL du Web Service d'authentification
	 */
	public void setURLStart(String value);

	/**
	 * Permet de positionner la durée de validité de la requête d'authentification
	 * @param value La durée de validité de la requête d'authentification
	 */
	public void setValidity(int value);
}