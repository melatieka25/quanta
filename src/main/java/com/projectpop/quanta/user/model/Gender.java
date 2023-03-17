package com.projectpop.quanta.user.model;

public enum Gender {
    MALE("male"),
    FEMALE("female");

    private String name;

    Gender(String name) {
        this.name = name;
    }

    public String geGenderName() {
        return name;
    }
}
