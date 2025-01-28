package fr.mpau.bean

/**
 * Classe modèle métier d'une journée de travail
 *
 * Author: Jonathan
 * Created: 11/02/2018
 * Last Updated: 24/01/2025
 */
data class WorkDay(

    /**
     * Attributs
     */

    val id: Int,
    val userWd: Int,
    val startWd: Long,
    val stopWd: Long,
    val finished: Boolean

)
