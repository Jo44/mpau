package fr.mpau.tool

import android.util.Log
import fr.mpau.bean.Timer
import fr.mpau.bean.WorkPeriod
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Tool - NumTools
 * -> getTimestampFromStringDateAndTime(String date, String time)
 * -> getListTimerDuration(List<Timer> listTimer)
 * -> getTimerDuration(Timer timer)
 * -> getWorkPeriodDuration(WorkPeriod wp)
 *
 * Author: Jonathan
 * Created: 11/02/2018
 * Last Updated: 24/01/2025
 */
object NumTools {

    /**
     * Attributs
     */

    private const val TAG = "NumTools"

    /**
     * Récupère le timestamp (en long) à partir des Date et Time en string
     *
     * @param date String?
     * @param time String?
     * @return Long
     */
    fun getTimestampFromStringDateAndTime(date: String?, time: String?): Long {
        var retour: Long = 0
        if (date != null && date.length == 10 && time != null && time.length == 5) {
            try {
                // Récupère les secondes actuelles pour compléter timestamp
                val now = Calendar.getInstance()
                val seconde = String.format(Locale.getDefault(), "%02d", now[Calendar.SECOND])
                val formattedDate =
                    date.substring(6, 10) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2)
                val formattedTime =
                    time.substring(0, 2) + ":" + time.substring(3, 5) + ":" + seconde
                val formattedCompletedDateTime = "$formattedDate $formattedTime"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val parsedDate = dateFormat.parse(formattedCompletedDateTime)
                retour = parsedDate!!.time / 1000
            } catch (e: ParseException) {
                Log.e(TAG, "Erreur de reconstitution du timestamp")
            }
        }
        return retour
    }

    /**
     * Renvoi la durée totale de travail de tous les timers (en secondes)
     *
     * @param listTimer List<Timer?>?
     * @return Long
     */
    fun getListTimerDuration(listTimer: List<Timer?>?): Long {
        var totalDuration = 0L
        if (!listTimer.isNullOrEmpty()) {
            for (timer in listTimer) {
                totalDuration += getTimerDuration(timer)
            }
        }
        return totalDuration
    }

    /**
     * Renvoi la durée totale de travail du timer (en secondes)
     *
     * @param timer Timer?
     * @return Long
     */
    fun getTimerDuration(timer: Timer?): Long {
        var totalDuration = 0L
        if (timer != null && timer.workperiodsList.isNotEmpty()) {
            val listWp: List<WorkPeriod> = timer.workperiodsList
            for (wp in listWp) {
                totalDuration += getWorkPeriodDuration(wp)
            }
        }
        return totalDuration
    }

    /**
     * Renvoi la durée totale de travail de la WorkPeriod (en secondes)
     *
     * @param wp WorkPeriod?
     * @return Long
     */
    fun getWorkPeriodDuration(wp: WorkPeriod?): Long {
        var duration = 0L
        if (wp != null && wp.startWp > 0 && wp.stopWp > 0) {
            duration = wp.stopWp - wp.startWp
        }
        return duration
    }

}
