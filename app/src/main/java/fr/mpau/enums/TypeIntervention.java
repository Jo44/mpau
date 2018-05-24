package fr.mpau.enums;

/**
 * Author: Jonathan B.
 * Created: 19/01/2018
 */

public enum TypeIntervention {
    SOS("SOS"),
    QUINZE("15");

    private String name = "";

    TypeIntervention(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
