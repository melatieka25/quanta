package com.projectpop.quanta.siswa.model;

import java.time.LocalDate;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SiswaCsvModel {


    @CsvBindByName
    private int id;

    @CsvBindByName
    private String address;

    @CsvBindByName
    private String fullName;

    @CsvBindByName
    @CsvDate("yyyy-MM-dd")
    private LocalDate dob;

    @CsvBindByName
    private String email;

    @CsvBindByName
    private String gender;

    @CsvBindByName
    private String nickname;
    
    @CsvBindByName
    private long phone_num;

    @CsvBindByName
    private String pob;

    @CsvBindByName
    private String religion;

    @CsvBindByName
    private String jenjang;

    @CsvBindByName
    private String sekolah;

    @CsvBindByName
    private String addressOrtu;

    @CsvBindByName
    private String fullNameOrtu;

    @CsvBindByName
    @CsvDate("yyyy-MM-dd")
    private LocalDate dobOrtu;

    @CsvBindByName
    private String emailOrtu;

    @CsvBindByName
    private String genderOrtu;

    @CsvBindByName
    private String nicknameOrtu;
    
    @CsvBindByName
    private long phone_numOrtu;

    @CsvBindByName
    private String pobOrtu;

    @CsvBindByName
    private String religionOrtu;

    @CsvBindByName
    private String jobOrtu;

    @CsvBindByName
    private String kantorOrtu;

}
