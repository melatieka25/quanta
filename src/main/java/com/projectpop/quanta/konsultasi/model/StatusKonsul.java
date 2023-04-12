package com.projectpop.quanta.konsultasi.model;

public enum StatusKonsul {
    PENDING("Pending"),
    DITERIMA("Diterima"),
    DITOLAK("Ditolak"),
    DITUTUP("Ditutup"),
    KADALUARSA("Kadaluarsa");

    private final String displayValue;
    StatusKonsul(String displayValue) {
        this.displayValue = displayValue;
    }
    public String getDisplayValue() {
        return displayValue;
    }

    }
