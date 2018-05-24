package fr.mpau.tools;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.mpau.models.Timer;
import fr.mpau.models.WorkPeriod;

/**
 * Tools NumTools
 * -> getTimestampFromStringDateAndTime(String date, String time)
 * -> getListTimerDuration(List<Timer> listTimer)
 * -> getTimerDuration(Timer timer)
 * -> getWorkPeriodDuration(WorkPeriod wp)
 * <p>
 * Author: Jonathan B.
 * Created: 11/02/2018
 */

public class NumTools {

    /**
     * Attributs
     */
    private static final String ERROR = "[ERROR]";

    /**
     * Récupère le timestamp (en long) à partir des Date et Time en string
     *
     * @param date String
     * @param time String
     * @return long
     */
    public static long getTimestampFromStringDateAndTime(String date, String time) {
        long retour = 0;
        if (date != null && date.length() == 10 && time != null && time.length() == 5) {
            try {
                // Récupère les secondes actuelles pour compléter timestamp
                Calendar now = Calendar.getInstance();
                String seconde = String.format(Locale.getDefault(), "%02d", now.get(Calendar.SECOND));
                String formattedDate = date.substring(6, 10) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2);
                String formattedTime = time.substring(0, 2) + ":" + time.substring(3, 5) + ":" + seconde;
                String formattedCompletedDateTime = formattedDate + " " + formattedTime;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date parsedDate = dateFormat.parse(formattedCompletedDateTime);
                retour = parsedDate.getTime() / 1000;
            } catch (ParseException e) {
                Log.e(ERROR, "Erreur de reconstitution du timestamp");
            }
        }
        return retour;
    }

    /**
     * Renvoi la durée totale de travail de tous les timers
     *
     * @param listTimer List<Timer>
     * @return long
     */
    public static long getListTimerDuration(List<Timer> listTimer) {
        long totalDuration = 0L;
        if (listTimer != null && listTimer.size() > 0) {
            for (Timer timer : listTimer) {
                totalDuration += getTimerDuration(timer);
            }
        }
        return totalDuration;
    }

    /**
     * Renvoi la durée totale de travail du timer
     *
     * @param timer Timer
     * @return long
     */
    public static long getTimerDuration(Timer timer) {
        long totalDuration = 0L;
        if (timer != null && timer.getTimerWorkperiodsList() != null && timer.getTimerWorkperiodsList().size() > 0) {
            List<WorkPeriod> listWp = timer.getTimerWorkperiodsList();
            for (WorkPeriod wp : listWp) {
                totalDuration += getWorkPeriodDuration(wp);
            }
        }
        return totalDuration;
    }

    /**
     * Renvoi la durée totale de travail de la WorkPeriod
     *
     * @param wp WorkPeriod
     * @return long
     */
    public static long getWorkPeriodDuration(WorkPeriod wp) {
        long duration = 0L;
        if (wp != null && wp.getWpStart() > 0 && wp.getWpStop() > 0) {
            duration = wp.getWpStop() - wp.getWpStart();
        }
        return duration;
    }

}
