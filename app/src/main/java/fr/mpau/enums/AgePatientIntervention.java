package fr.mpau.enums;

/**
 * Author: Jonathan B.
 * Created: 19/01/2018
 */

public enum AgePatientIntervention {
    _00_10("00 — 10"),
    _10_20("10 — 20"),
    _20_30("20 — 30"),
    _30_40("30 — 40"),
    _40_50("40 — 50"),
    _50_60("50 — 60"),
    _60_70("60 — 70"),
    _70_80("70 — 80"),
    _80_90("80 — 90"),
    _90_100("90 — 100"),
    _PLUS_100("+ 100");

    private String name = "";

    AgePatientIntervention(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
