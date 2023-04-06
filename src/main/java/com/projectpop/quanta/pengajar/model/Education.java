package com.projectpop.quanta.pengajar.model;

public enum Education {
    SARJANA ("Sarjana (S1)"),
    MAGISTER ("Magister (S2)"),
    DOKTOR ("Doktor (S3)");

    private final String displayValue;

    Education(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
