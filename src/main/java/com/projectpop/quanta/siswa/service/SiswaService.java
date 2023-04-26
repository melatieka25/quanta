package com.projectpop.quanta.siswa.service;

import java.util.List;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.siswa.model.SiswaCsvModel;
import com.projectpop.quanta.siswa.model.SiswaModel;

public interface SiswaService {

    void addSiswa(SiswaModel siswa);
    List<SiswaModel> getListSiswa();
    KelasModel getKelasBimbel(SiswaModel siswa);
    SiswaModel getSiswaById(int id);
    SiswaModel inactiveSiswa(SiswaModel siswa);
    SiswaModel activeSiswa(SiswaModel siswa);
    SiswaModel updateSiswa(SiswaModel siswa);
    int getNumberOfKonsultasiAktif(SiswaModel siswa);
    SiswaModel findSiswaByEmail(String email);
    List<SiswaModel> getListSiswaExsAndNoClass(List<SiswaModel> listSiswa, KelasModel kelas);
    List<SiswaModel> getListSiswaActive();
    SiswaModel getSiswaByEmail(String email);
    SiswaModel convertSiswaCsv(SiswaCsvModel siswaCsv);

}
