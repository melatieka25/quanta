package com.projectpop.quanta.user.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatePasswordModel {
    private String passwordLama;
    private String passwordBaru;
    private String konfirmasiPasswordBaru;
}
