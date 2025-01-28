package fr.mpau.enums

/**
 * Enumération des types d'intervention
 *
 * Author: Jonathan
 * Created: 19/01/2018
 * Last Updated: 24/01/2025
 */
enum class TypeIntervention(private val value: String) {

    /**
     * Attributs
     */

    SOS("SOS"),
    QUINZE("15");

    /**
     * Retourne la valeur associée
     *
     * @return String
     */
    fun getValue(): String {
        return value
    }

}