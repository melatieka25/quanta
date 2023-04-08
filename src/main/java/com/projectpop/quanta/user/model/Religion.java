package com.projectpop.quanta.user.model;

public enum Religion {
    ISLAM ("Islam"),
    KATOLIK ("Katolik"),
    PROTESTAN ("Protestan"),
    BUDHA ("Budha"),
    HINDU ("Hindu"),
    KONGHUCU ("Konghucu");

    private final String displayValue;

    Religion(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
