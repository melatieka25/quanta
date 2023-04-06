package com.projectpop.quanta.siswakonsultasi.repository;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiswaKonsultasiDb extends JpaRepository<SiswaKonsultasiModel, Integer> {
    List<SiswaKonsultasiModel> findAllByKonsultasi(KonsultasiModel konsultasi);

    List<SiswaKonsultasiModel> findAllBySiswaKonsul(SiswaModel siswa);

    SiswaKonsultasiModel getByKonsultasiAndSiswaKonsul(KonsultasiModel konsultasi, SiswaModel siswa);

}
