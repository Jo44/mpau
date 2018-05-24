package fr.mpau.exceptions;

/**
 * Classe d'exception technique
 *
 * Author: Jonathan B.
 * Created: 12/02/2018
 */

public class TechnicalException extends Exception {

    /**
     * Attributs
     */
    private String message;

    /**
     * Constructeur
     *
     * @param message String
     */
    public TechnicalException(String message) {
        this.message = message;
    }

    /**
     * Getter / Setter
     */
    public String getMessage() {
        return message;
    }

}
