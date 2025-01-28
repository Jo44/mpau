package fr.mpau.bean

/**
 * Classe modèle métier d'un utilisateur
 *
 * Author: Jonathan
 * Created: 25/01/2018
 * Last Updated: 24/01/2025
 */
data class User(

    /**
     * Attributs
     */

    val id: Int,
    val name: String,
    val pass: String,
    val email: String,
    val nbInter: Int,
    val interIdMax: Int,
    val inscriptionDate: Long

)
