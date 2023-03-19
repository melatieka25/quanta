package com.projectpop.quanta.konsultasi.repository;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.model.StatusKonsul;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KonsultasiDb extends JpaRepository<KonsultasiModel, Integer> {

    List<KonsultasiModel> findAllByPengajarKonsul(PengajarModel pengajar);

    KonsultasiModel getById(Integer id);

}
