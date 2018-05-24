package fr.mpau.models;

/**
 * Classe modèle métier d'une période de travail
 * <p>
 * Author: Jonathan B.
 * Created: 11/02/2018
 */

public class WorkPeriod {

    /**
     * Attributs
     */
    private int wpId;
    private int wpWdId;
    private long wpStart;
    private long wpStop;
    private boolean wpFinished;

    /**
     * Constructeurs
     */
    public WorkPeriod(int wpId, int wpWdId, long wpStart, long wpStop, boolean wpFinished) {
        this.wpId = wpId;
        this.wpWdId = wpWdId;
        this.wpStart = wpStart;
        this.wpStop = wpStop;
        this.wpFinished = wpFinished;
    }

    /**
     * Getters
     */
    public int getWpId() {
        return wpId;
    }

    public int getWpWdId() {
        return wpWdId;
    }

    public long getWpStart() {
        return wpStart;
    }

    public long getWpStop() {
        return wpStop;
    }

    public boolean isWpFinished() {
        return wpFinished;
    }
}
