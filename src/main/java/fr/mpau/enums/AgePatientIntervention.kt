package fr.mpau.enums

/**
 * Enumération des ages des patients
 *
 * Author: Jonathan
 * Created: 19/01/2018
 * Last Updated: 24/01/2025
 */
enum class AgePatientIntervention(private val value: String) {

    /**
     * Attributs
     */

    A_00_10("00 / 10"),
    A_10_20("10 / 20"),
    A_20_30("20 / 30"),
    A_30_40("30 / 40"),
    A_40_50("40 / 50"),
    A_50_60("50 / 60"),
    A_60_70("60 / 70"),
    A_70_80("70 / 80"),
    A_80_90("80 / 90"),
    A_90_100("90 / 100"),
    A_PLUS_100("+ 100");

    /**
     * Retourne la valeur associée
     *
     * @return String
     */
    fun getValue(): String {
        return value
    }

}