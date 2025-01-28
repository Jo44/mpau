package fr.mpau.enums

/**
 * Enumération des sous-types d'intervention
 *
 * Author: Jonathan
 * Created: 19/01/2018
 * Last Updated: 24/01/2025
 */
enum class SubtypeIntervention(private val value: String) {

    /**
     * Attributs
     */

    DETRESSE_CARDIO("Détresse Cardiologique"),
    DETRESSE_NEURO("Détresse Neurologique"),
    DETRESSE_RESPI("Détresse Respiratoire"),
    PETIT_BOBO("Petit Bobo"),
    PLAIE("Plaie"),
    PSYCHIATRIE("Psychiatrie"),
    TRAUMATO("Traumatologie"),
    AUTRE("Autre");

    /**
     * Retourne la valeur associée
     *
     * @return String
     */
    fun getValue(): String {
        return value
    }

}