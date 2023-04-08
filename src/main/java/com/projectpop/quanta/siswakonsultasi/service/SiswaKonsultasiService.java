package com.projectpop.quanta.siswakonsultasi.service;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;

import java.util.List;

public interface SiswaKonsultasiService {

    SiswaKonsultasiModel getById(Integer id);
    SiswaKonsultasiModel getBySiswaAndKonsultasi(SiswaModel siswa, Integer idKonsultasi);
    List<SiswaKonsultasiModel> getListSiswaByKonsultasi(KonsultasiModel konsultasi);
    Integer getJumlahSiswaKonsultasi(KonsultasiModel idKonsultasi);

    List<SiswaKonsultasiModel> getListKonsultasiBySiswa(SiswaModel siswa);

    SiswaKonsultasiModel cancelConsultation(SiswaKonsultasiModel siswaKonsultasi);

    SiswaKonsultasiModel createSiswaKonsultasi(SiswaKonsultasiModel siswaKonsultasi);

}
