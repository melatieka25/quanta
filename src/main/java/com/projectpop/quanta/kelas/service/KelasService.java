package com.projectpop.quanta.kelas.service;

import com.projectpop.quanta.kelas.model.KelasModel;

import java.util.List;

public interface KelasService {
    void addKelas(KelasModel kelas);
    List<KelasModel> getAllKelas();

    KelasModel getKelasById(Integer id);

    void deleteKelas(KelasModel kelas);

}
