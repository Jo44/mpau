package fr.mpau.tools.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.mpau.R;
import fr.mpau.models.Timer;
import fr.mpau.models.WorkPeriod;
import fr.mpau.tools.NumTools;
import fr.mpau.tools.StringTools;

/**
 * Adapter pour afficher la ListView des timers
 * <p>
 * Author: Jonathan B.
 * Created: 11/02/2018
 */

public class TimersAdapter extends ArrayAdapter<Timer> {

    /**
     * Attributs
     */
    private final Context context;
    private ArrayList<Timer> listTimers;

    /**
     * Constructeur
     *
     * @param context    Context
     * @param resource   int
     * @param listTimers ArrayList<Timer>
     */
    public TimersAdapter(Context context, int resource, ArrayList<Timer> listTimers) {
        super(context, resource, listTimers);
        this.context = context;
        this.listTimers = listTimers;
    }

    /**
     * Construction d'une entrée (une ligne de timer)
     *
     * @param position    int
     * @param convertView View
     * @param parent      ViewGroup
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(R.layout.row_timer, parent, false);
            }
        } else {
            convertView = (RelativeLayout) convertView;
        }
        // Récupération des éléments du layout
        TextView timerDay = convertView.findViewById(R.id.timerDay);
        TextView timerTime = convertView.findViewById(R.id.timerTime);
        TextView timerDetails = convertView.findViewById(R.id.timerDetails);
        // Récupération de l'item Timer
        Timer timer = listTimers.get(position);
        // Inflate le layout
        timerDay.setText(getStartDate(timer));
        timerTime.setText(getTimerDuration(timer));
        timerDetails.setText(getDetails(timer));
        return convertView;
    }

    /**
     * Renvoi la date de début du timer au format 'dd/MM'
     *
     * @param timer Timer
     * @return String
     */
    private String getStartDate(Timer timer) {
        String date = "";
        if (timer != null && timer.getTimerWorkday() != null && timer.getTimerWorkday().getWdStart() > 0) {
            // Récupère le timestamp de début de la WorkDay
            long startDateTimestamp = timer.getTimerWorkday().getWdStart();
            // Converti au format 'dd/MM'
            date = StringTools.getShortDateStringFromTimestamp(startDateTimestamp);
        }
        return date;
    }

    /**
     * Renvoi la durée totale de travail du timer au format 'HHhmm' (ex: 17h30)
     *
     * @param timer Timer
     * @return String
     */
    private String getTimerDuration(Timer timer) {
        String duration;
        long totalDuration = NumTools.getTimerDuration(timer);
        // Transforme la différence de seconde à heure / minute
        long hour = totalDuration / 3600;
        long minute = (totalDuration % 3600) / 60;
        duration = String.valueOf(hour) + "h" + String.format(Locale.getDefault(), "%02d", minute);
        return duration;
    }

    /**
     * Met à forme le détail des WorkPeriods sous forme de String
     *
     * @param timer Timer
     * @return String
     */
    private String getDetails(Timer timer) {
        String details = "";
        if (timer != null && timer.getTimerWorkperiodsList() != null && timer.getTimerWorkperiodsList().size() > 0) {
            List<WorkPeriod> listWp = timer.getTimerWorkperiodsList();
            StringBuilder stringBuilder = new StringBuilder();
            // Récupère la différence (en secondes) entre le début et la fin de chaque WorkPeriod
            for (WorkPeriod wp : listWp) {
                if (wp != null) {
                    stringBuilder.append("- de ");
                    stringBuilder.append(getTime(wp, true));
                    stringBuilder.append(" à ");
                    stringBuilder.append(getTime(wp, false));
                    stringBuilder.append("  (");
                    stringBuilder.append(getWpDuration(wp));
                    stringBuilder.append(")\n");
                }
            }
            details = stringBuilder.toString();
        }
        return details;
    }

    /**
     * Renvoi l'heure de début du timer au format 'HH:mm' (ex: 17:30)
     *
     * @param wp WorkPeriod
     * @return String
     */
    private String getTime(WorkPeriod wp, boolean start) {
        String date = "";
        if (wp != null && wp.getWpStart() > 0 && wp.getWpStop() > 0) {
            // Récupère le timestamp de début/fin de la WorkPeriod
            long timeWp;
            if (start) {
                timeWp = wp.getWpStart();
            } else {
                timeWp = wp.getWpStop();
            }
            // Converti au format 'HHhmm'
            date = StringTools.getTimeStringFromTimestamp(timeWp, ":");
        }
        return date;
    }

    /**
     * Renvoi la durée totale d'une WorkPeriod au format 'HHhmm' (ex: 17h30)
     *
     * @param wp WorkPeriod
     * @return String
     */
    private String getWpDuration(WorkPeriod wp) {
        String duration;
        long durationLong = NumTools.getWorkPeriodDuration(wp);
        // Transforme la différence de seconde à heure / minute
        long hour = durationLong / 3600;
        long minute = (durationLong % 3600) / 60;
        duration = String.valueOf(hour) + "h" + String.format(Locale.getDefault(), "%02d", minute);
        return duration;
    }

}