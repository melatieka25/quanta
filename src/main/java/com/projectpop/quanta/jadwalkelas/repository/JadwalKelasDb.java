package com.projectpop.quanta.jadwalkelas.repository;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JadwalKelasDb extends JpaRepository <JadwalKelasModel, Long> {
    List<JadwalKelasModel> findAllByPengajarKelas(PengajarModel pengajarKelas);
    JadwalKelasModel findById(Integer ID);
}
