package fr.mpau.bean

/**
 * Classe modèle métier d'une période de travail
 *
 * Author: Jonathan
 * Created: 11/02/2018
 * Last Updated: 24/01/2025
 */
data class WorkPeriod(

    /**
     * Attributs
     */

    val id: Int,
    val wpWdId: Int,
    val startWp: Long,
    val stopWp: Long,
    val finished: Boolean

)
