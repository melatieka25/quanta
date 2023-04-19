package com.projectpop.quanta.pengajar.model;

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
public class PengajarCsvModel {


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
    private String jurusan;

    @CsvBindByName
    private long ktp;

    @CsvBindByName
    private String lastEdu;

    @CsvBindByName
    private String status;

    @CsvBindByName
    private String university;
}
