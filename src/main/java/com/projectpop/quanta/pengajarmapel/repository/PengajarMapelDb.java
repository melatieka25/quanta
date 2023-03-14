package com.projectpop.quanta.pengajarmapel.repository;

import org.springframework.stereotype.Repository;

import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface PengajarMapelDb extends JpaRepository<PengajarMapelModel, String> {
    List<PengajarMapelModel> findByMapel(MataPelajaranModel mapel);
    List<PengajarMapelModel> findByPengajar(PengajarModel pengajar);

}
