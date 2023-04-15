package com.projectpop.quanta.presensi.model;

public enum PresensiStatus {
    HADIR("HADIR"),
    ALPHA("ALPHA"),
    SAKIT("SAKIT"),
    IZIN("IZIN"),
    KOSONG("KOSONG");

    private final String displayValue;

    PresensiStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue(){
        return displayValue;
    }
}
