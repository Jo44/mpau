package fr.mpau.models;

import java.util.List;

/**
 * Classe modèle métier d'un timer (timer = workday + workperiods)
 * <p>
 * Author: Jonathan B.
 * Created: 11/02/2018
 */

public class Timer {

    /**
     * Attributs
     */
    private int timerId;
    private WorkDay timerWorkday;
    private List<WorkPeriod> timerWorkperiodsList;

    /**
     * Constructeurs
     */
    public Timer(int timerId, WorkDay timerWorkday, List<WorkPeriod> timerWorkperiodsList) {
        this.timerId = timerId;
        this.timerWorkday = timerWorkday;
        this.timerWorkperiodsList = timerWorkperiodsList;
    }

    /**
     * Getters
     */
    public int getTimerId() {
        return timerId;
    }

    public WorkDay getTimerWorkday() {
        return timerWorkday;
    }

    public List<WorkPeriod> getTimerWorkperiodsList() {
        return timerWorkperiodsList;
    }
}
