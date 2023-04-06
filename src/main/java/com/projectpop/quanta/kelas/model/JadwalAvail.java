package com.projectpop.quanta.kelas.model;

public enum JadwalAvail {
    SeninRabuJumat("Senin-Rabu-Jumat"),
    SelasaKamisSabtu("Selasa-Kamis-Sabtu");

    private final String displayValue;

    JadwalAvail(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

}
