package fr.mpau.bean

/**
 * Classe modèle métier d'une intervention
 *
 * Author: Jonathan
 * Created: 19/01/2018
 * Last Updated: 24/01/2025
 */
data class Intervention(

    /**
     * Attributs
     */

    val id: Int,
    val userInter: Int,
    val dateInter: Long,
    val duree: Int,
    val secteur: String,
    val smur: Boolean,
    val mainTypeId: Int,
    val sousTypeId: Int,
    val agePatientId: Int,
    val commentaire: String

)
