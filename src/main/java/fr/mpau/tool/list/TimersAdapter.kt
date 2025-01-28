package fr.mpau.tool.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import fr.mpau.R
import fr.mpau.bean.Timer
import fr.mpau.bean.WorkPeriod
import fr.mpau.tool.NumTools
import fr.mpau.tool.StringTools
import java.util.Locale

/**
 * Tool - Adaptateur pour afficher la ListView des timers
 *
 * Author: Jonathan
 * Created: 11/02/2018
 * Last Updated: 27/01/2025
 */
class TimersAdapter(private val context: Context, resource: Int, list: ArrayList<Timer>) :
    ArrayAdapter<Timer?>(context, resource, list as List<Timer?>) {

    /**
     * Attributs
     */

    private val listTimers: ArrayList<Timer> = list

    /**
     * Construction d'une entrée (une ligne de timer)
     *
     * @param position Int
     * @param convertView View?
     * @param parent ViewGroup
     * @return View
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.row_timer, parent, false)
        } else {
            view = view as RelativeLayout
        }
        // Récupération des éléments du layout
        val timerDay = view!!.findViewById<TextView>(R.id.timerDay)
        val timerTime = view.findViewById<TextView>(R.id.timerTime)
        val timerDetails = view.findViewById<TextView>(R.id.timerDetails)
        // Récupération de l'item Timer
        val timer: Timer = listTimers[position]
        // Inflate le layout
        timerDay.text = getStartDate(timer)
        timerTime.text = getTimerDuration(timer)
        timerDetails.text = getDetails(timer)
        return view
    }

    /**
     * Renvoi la date de début du timer au format 'dd/MM'
     *
     * @param timer Timer
     * @return String
     */
    private fun getStartDate(timer: Timer): String {
        var date = ""
        if (timer.workday.startWd > 0) {
            // Récupère le timestamp de début de la WorkDay
            val startDateTimestamp: Long = timer.workday.startWd
            // Converti au format 'dd/MM'
            date = StringTools.getShortDateStringFromTimestamp(startDateTimestamp)
        }
        return date
    }

    /**
     * Renvoi la durée totale de travail du timer au format 'HHhmm' (ex: 17h30)
     *
     * @param timer Timer
     * @return String
     */
    private fun getTimerDuration(timer: Timer): String {
        val duration: String
        val totalDuration: Long = NumTools.getTimerDuration(timer)
        // Transforme la différence de seconde à heure / minute
        val hour = totalDuration / 3600
        val minute = (totalDuration % 3600) / 60
        duration = hour.toString() + "h" + String.format(Locale.getDefault(), "%02d", minute)
        return duration
    }

    /**
     * Met à forme le détail des WorkPeriods sous forme de String
     *
     * @param timer Timer
     * @return String
     */
    private fun getDetails(timer: Timer): String {
        var details = ""
        if (timer.workperiodsList.isNotEmpty()
        ) {
            val listWp: List<WorkPeriod> = timer.workperiodsList
            val stringBuilder = StringBuilder()
            // Récupère la différence (en secondes) entre le début et la fin de chaque WorkPeriod
            for (wp in listWp) {
                stringBuilder.append("->  ")
                stringBuilder.append(getTime(wp, true))
                stringBuilder.append("  -  ")
                stringBuilder.append(getTime(wp, false))
                stringBuilder.append("  [")
                stringBuilder.append(getWpDuration(wp))
                stringBuilder.append("]\n")
            }
            details = stringBuilder.toString()
        }
        return details
    }

    /**
     * Renvoi l'heure de début du timer au format 'HH:mm' (ex: 17:30)
     *
     * @param wp WorkPeriod
     * @param start Boolean
     * @return String
     */
    private fun getTime(wp: WorkPeriod, start: Boolean): String {
        var date = ""
        if (wp.startWp > 0 && wp.stopWp > 0) {
            // Récupère le timestamp de début/fin de la WorkPeriod
            val timeWp: Long = if (start) {
                wp.startWp
            } else {
                wp.stopWp
            }
            // Converti au format 'HHhmm'
            date = StringTools.getTimeStringFromTimestamp(timeWp, ":")
        }
        return date
    }

    /**
     * Renvoi la durée totale d'une WorkPeriod au format 'HHhmm' (ex: 17h30)
     *
     * @param wp WorkPeriod
     * @return String
     */
    private fun getWpDuration(wp: WorkPeriod): String {
        val duration: String
        val durationLong: Long = NumTools.getWorkPeriodDuration(wp)
        // Transforme la différence de seconde à heure / minute
        val hour = durationLong / 3600
        val minute = (durationLong % 3600) / 60
        duration = hour.toString() + "h" + String.format(Locale.getDefault(), "%02d", minute)
        return duration
    }

}