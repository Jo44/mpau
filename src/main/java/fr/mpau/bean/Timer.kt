package fr.mpau.bean

/**
 * Classe modèle métier d'un timer (timer = workday + workperiods)
 *
 * Author: Jonathan
 * Created: 11/02/2018
 * Last Updated: 24/01/2025
 */
data class Timer(

    /**
     * Attributs
     */

    val id: Int,
    val workday: WorkDay,
    val workperiodsList: List<WorkPeriod>

)
