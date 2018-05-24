package fr.mpau.enums;

/**
 * Author: Jonathan B.
 * Created: 19/01/2018
 */

public enum SubtypeIntervention {
    DETRESSE_CARDIO("Détresse Cardiologique"),
    DETRESSE_NEURO("Détresse Neurologique"),
    DETRESSE_RESPI("Détresse Respiratoire"),
    PETIT_BOBO("Petit Bobo"),
    PLAIE("Plaie"),
    PSYCHIATRIE("Psychiatrie"),
    TRAUMATO("Traumatologie"),
    AUTRE("Autre");

    private String name = "";

    SubtypeIntervention(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
