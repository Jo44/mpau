package fr.mpau.models;

/**
 * Classe modèle métier du mode du timer
 * <p>
 * Author: Jonathan B.
 * Created: 25/01/2018
 */

public class TimerMode {
    /**
     * Attributs
     */
    private String mode;
    private long lastTimestamp;

    /**
     * Constructeur
     */
    public TimerMode(String mode, long lastTimestamp) {
        this.mode = mode;
        this.lastTimestamp = lastTimestamp;
    }

    /**
     * Getters
     */
    public String getMode() {
        return mode;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }
}
