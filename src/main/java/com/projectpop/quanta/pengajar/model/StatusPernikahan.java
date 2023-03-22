package com.projectpop.quanta.pengajar.model;

public enum StatusPernikahan {
    BELUM_KAWIN ("Belum Menikah"),
    KAWIN ("Menikah"),
    CERAI_HIDUP ("Cerai Hidup"),
    CERAI_MATI ("Cerai Mati");

    private final String displayValue;

    StatusPernikahan(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

}
