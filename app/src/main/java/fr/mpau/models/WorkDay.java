package fr.mpau.models;

/**
 * Classe modèle métier d'une journée de travail
 * <p>
 * Author: Jonathan B.
 * Created: 11/02/2018
 */

public class WorkDay {

    /**
     * Attributs
     */
    private int wdId;
    private int wdUserId;
    private long wdStart;
    private long wdStop;
    private boolean wdFinished;

    /**
     * Constructeurs
     */
    public WorkDay(int wdId, int wdUserId, long wdStart, long wdStop, boolean wdFinished) {
        this.wdId = wdId;
        this.wdUserId = wdUserId;
        this.wdStart = wdStart;
        this.wdStop = wdStop;
        this.wdFinished = wdFinished;
    }

    /**
     * Getters
     */
    public int getWdId() {
        return wdId;
    }

    public int getWdUserId() {
        return wdUserId;
    }

    public long getWdStart() {
        return wdStart;
    }

    public long getWdStop() {
        return wdStop;
    }

    public boolean isWdFinished() {
        return wdFinished;
    }
}
