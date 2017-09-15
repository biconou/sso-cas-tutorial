package sopra.immobilier.ws.core;

import org.apache.commons.lang.StringUtils;

/**
 * Message d'authentification d'un utilisateur sur un service web.
 * 
 * <p>Ce message comprend au minimum un statut définissant son état :
 * <ul>
 *   <li><code>AUTHENTICATED</code> : Utilisateur authentifié</li>
 *   <li><code>NOT_AUTHENTICATED</code> : Utilisateur non authentifié</li>
 *   <li><code>ERROR</code> : Une erreur est survenue lors de l'authentification</li>
 * </ul>
 * </p>
 * 
 * <p>Il peut aussi contenir un message d'information précisant les conditions d'erreur le cas échéant.<br />
 * <b>Attention</b>, les erreurs possibles sont purements techniques, les erreurs fonctionnelles sont définies par le statut <code>NOT_AUTHENTICATED</code>.</p>
 * 
 * <TABLE>
 *   <TR>
 *     <TD><B>Attributs PVCS :</B></TD>
 *     <TD>&nbsp;</TD>
 *   </TR>
 *   <TR>
 *     <TD><I>Nom du fichier :</I></TD>
 *     <TD>$Archive:   O:/XNET/XREF/archives/cas/WEB-INF/classes/sopra/immobilier/ws/core/WSAuthenticationMessage.java-arc  $</TD>
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
public class WSAuthenticationMessage {
	// Constantes
	/** Version de la classe extraite du gestionnaire de configuration */
	public static final String CLASS_VERSION = "$Revision:   1.0  $";

	/**
	 * Création d'un message d'authentification par défaut.
	 * Il aura le statut <code>Statut.NOT_AUTHENTICATED</code>.
	 */
	public WSAuthenticationMessage() {
		this(false);
	}

	/**
	 * Création d'un message d'authentification.
	 * 
	 * @param authenticated <code>true</code> si l'authentification a réussi, <code>false</code>
	 */
	public WSAuthenticationMessage(boolean authenticated) {
		this(authenticated, StringUtils.EMPTY);
	}

	/**
	 * Création d'un message d'authentification.
	 * 
	 * @param authenticated <code>true</code> si l'authentification a réussi, <code>false</code>
	 * @param message Un message en cas d'erreur technique lors de l'authentification (or erreur fonctionnelle d'authentification)
	 */
	public WSAuthenticationMessage(boolean authenticated, String message) {
		if (authenticated) {
			setStatut(Statut.AUTHENTICATED);
			setMessage(StringUtils.EMPTY);
		} else if (StringUtils.isNotBlank(message)) {
			setStatut(Statut.ERROR);
			setMessage(message);
		} else {
			setStatut(Statut.NOT_AUTHENTICATED);
			setMessage(StringUtils.EMPTY);
		}
	}

	/** Le statut de l'authentification */
	private Statut mStatut = null;
	/** Le message associé à un statut d'authentification en erreur */
	private String mMessage = null;

	/**
	 * Retourne le statut de l'authentification.
	 * @return Le statut de l'authentification
	 */
	public Statut getStatut() {
		return mStatut;
	}

	/**
	 * Positionne le statut de l'authentification.
	 * @param mStatut Le statut de l'authentification
	 */
	public void setStatut(Statut mStatut) {
		this.mStatut = mStatut;
	}

	/**
	 * Retourne le message d'information associé à une erreur d'authentification.
	 * @return Le message d'information
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * Positionne le message d'information associé à une erreur d'authentification.
	 * @param mMessage Le message d'information
	 */
	public void setMessage(String mMessage) {
		this.mMessage = mMessage;
	}

	/**
	 * Enumération des statuts possibles d'un message d'authentification.
	 * 
	 * <ul>
	 *   <li><code>AUTHENTICATED</code> : Utilisateur authentifié</li>
	 *   <li><code>NOT_AUTHENTICATED</code> : Utilisateur non authentifié</li>
	 *   <li><code>ERROR</code> : Une erreur est survenue lors de l'authentification</li>
	 * </ul>
	 * 
	 * <TABLE>
	 *   <TR>
	 *     <TD><B>Attributs PVCS :</B></TD>
	 *     <TD>&nbsp;</TD>
	 *   </TR>
	 *   <TR>
	 *     <TD><I>Nom du fichier :</I></TD>
	 *     <TD>$Archive:   O:/XNET/XREF/archives/cas/WEB-INF/classes/sopra/immobilier/ws/core/WSAuthenticationMessage.java-arc  $</TD>
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
	public static enum Statut {
		AUTHENTICATED(true), NOT_AUTHENTICATED(false), ERROR(false);

		/** Valeur booléenne du statut */
		private final boolean mValue; 

		/** Construction suivant une valeur booléenne donnée */
		private Statut(boolean value) {
			this.mValue = value;
		}

		/**
		 * Retourne la valeur booléenne du statut.
		 * @return <code>true</code> si l'authentification a réussi, <code>false</code> sinon
		 */
		public boolean booleanValue() {
			return this.mValue;
		}
	}
}