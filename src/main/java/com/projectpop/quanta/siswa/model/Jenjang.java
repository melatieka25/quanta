package com.projectpop.quanta.siswa.model;

public enum Jenjang {
    SMP_8("8 SMP"),
    SMP_9("9 SMP"),
    SMA_10("10 SMA"),
    SMA_11_IPA("11 SMA IPA"),
    SMA_11_IPS("11 SMA IPS"),
    SMA_12_IPA("12 SMA 1PA"),
    SMA_12_IPS("12 SMA IPA");

    private final String displayValue;

    Jenjang(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
