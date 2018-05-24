package fr.mpau.exceptions;

/**
 * Classe d'exception fonctionnelle
 * <p>
 * Author: Jonathan B.
 * Created: 12/02/2018
 */

public class FonctionnalException extends Exception {

    /**
     * Attributs
     */
    private String message;

    /**
     * Constructeur
     *
     * @param message String
     */
    public FonctionnalException(String message) {
        this.message = message;
    }

    /**
     * Getter / Setter
     */
    public String getMessage() {
        return message;
    }

}
