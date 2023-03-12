package com.projectpop.quanta.siswa.service;

import java.util.List;

import com.projectpop.quanta.siswa.model.SiswaModel;

public interface SiswaService {

    void addSiswa(SiswaModel siswa);
    List<SiswaModel> getListSiswa();
    String getKelasBimbel(SiswaModel siswa);

}
