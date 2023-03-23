package com.projectpop.quanta.user.model;

public enum Gender {
    MALE("Laki-Laki"),
    FEMALE("Perempuan");

    private final String displayValue;

    Gender(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
