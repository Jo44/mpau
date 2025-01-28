package fr.mpau.webservice

/**
 * Classe de résultat d'une requête au WebService MPAU-WS
 *
 * Author: Jonathan
 * Created: 24/01/2025
 * Last Updated: 24/01/2025
 */
class WSResult<T> {

    /**
     * Attributs
     */

    var result: T? = null
    var error: Exception? = null

    /**
     * Constructeurs
     */
    constructor(result: T) : super() {
        this.result = result
    }

    constructor(error: Exception?) : super() {
        this.error = error
    }

}