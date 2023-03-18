package com.projectpop.quanta.siswakelas.service;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;

import java.util.List;

public interface SiswaKelasService {

    List<SiswaKelasModel> getAllbyKelas(KelasModel kelas);
    SiswaKelasModel getSiswaKelasById(Integer id);

    void deleteAllByKelasSiswa(KelasModel kelas);
}
