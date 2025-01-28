package fr.mpau.tool

import android.util.Log
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Calendar
import java.util.Locale

/**
 * Tool - StringTools
 * -> capitalizeFirstLetter(String value)
 * -> getDateStringFromTimestamp(long timestamp)
 * -> getShortDateStringFromTimestamp(long timestamp)
 * -> getTimeStringFromTimestamp(long timestamp, String separator)
 * -> hashMD5(String value)
 * -> formatComment(String rawComment)
 *
 * Author: Jonathan
 * Created: 19/11/2017
 * Last Updated: 24/01/2025
 */
object StringTools {

    /**
     * Attributs
     */

    private const val TAG = "StringTools"

    /**
     * Met en majuscule la première lettre d'un string
     *
     * @param value String?
     * @return String?
     */
    fun capitalizeFirstLetter(value: String?): String? {
        var retour: String? = null
        if (value != null) {
            if (value.isEmpty()) {
                retour = value
            } else {
                val result = StringBuilder(value)
                result.replace(0, 1, result.substring(0, 1).uppercase(Locale.getDefault()))
                retour = result.toString()
            }
        }
        return retour
    }

    /**
     * Récupère la date en String au format "dd/MM/yyyy" à partir d'un timestamp
     *
     * @param timestamp Long
     * @return String
     */
    fun getDateStringFromTimestamp(timestamp: Long): String {
        var retour = ""
        if (timestamp != 0L) {
            val datetime = Calendar.getInstance()
            datetime.timeInMillis = timestamp * 1000
            retour = String.format(Locale.getDefault(), "%02d", datetime[Calendar.DAY_OF_MONTH]) +
                    "/" + String.format(
                Locale.getDefault(),
                "%02d",
                (datetime[Calendar.MONTH] + 1)
            ) +
                    "/" + datetime[Calendar.YEAR]
        }
        return retour
    }

    /**
     * Récupère la date en String au format court "dd/MM" à partir d'un timestamp
     *
     * @param timestamp Long
     * @return String
     */
    fun getShortDateStringFromTimestamp(timestamp: Long): String {
        var retour = ""
        if (timestamp != 0L) {
            val datetime = Calendar.getInstance()
            datetime.timeInMillis = timestamp * 1000
            retour = String.format(Locale.getDefault(), "%02d", datetime[Calendar.DAY_OF_MONTH]) +
                    "/" + String.format(Locale.getDefault(), "%02d", (datetime[Calendar.MONTH] + 1))
        }
        return retour
    }

    /**
     * Récupère l'heure en String au format "HH:mm" à partir d'un timestamp et ':' = separator
     *
     * @param timestamp Long
     * @param separator String
     * @return String
     */
    fun getTimeStringFromTimestamp(timestamp: Long, separator: String): String {
        var retour = ""
        if (timestamp != 0L) {
            val datetime = Calendar.getInstance()
            datetime.timeInMillis = timestamp * 1000
            retour = String.format(Locale.getDefault(), "%02d", datetime[Calendar.HOUR_OF_DAY]) +
                    separator + String.format(
                Locale.getDefault(),
                "%02d",
                datetime[Calendar.MINUTE]
            )
        }
        return retour
    }

    /**
     * Permet de hasher un string en MD5
     *
     * @param value String?
     * @return String
     */
    fun hashMD5(value: String?): String {
        var hash = ""
        if (value != null) {
            try {
                val md = MessageDigest.getInstance("MD5")
                val messageDigest = md.digest(value.toByteArray())
                val number = BigInteger(1, messageDigest)
                hash = number.toString(16)
            } catch (e: NoSuchAlgorithmException) {
                val erreur = "Erreur de cryptage MD5"
                Log.e(TAG, erreur)
            }
        }
        return hash
    }

    /**
     * Formatte le commentaire pour ne conserver que 50 caractères maximum ou le message 'Pas de commentaire' (pour l'affichage dans la liste des interventions)
     *
     * @param value String?
     * @return String
     */
    fun formatComment(value: String?): String {
        var rawComment = value
        val comment: String
        if (rawComment == null || rawComment.trim { it <= ' ' }.isEmpty()) {
            comment = "(Pas de commentaire)"
        } else {
            rawComment = rawComment.replace("(\r\n|\n)".toRegex(), " ")
            comment = if (rawComment.trim { it <= ' ' }.length > 50) {
                rawComment.trim { it <= ' ' }.substring(0, 47) + "..."
            } else {
                rawComment.trim { it <= ' ' }
            }
        }
        return comment
    }

}
