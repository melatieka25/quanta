package com.projectpop.quanta.jadwalkelas.repository;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Repository
public interface JadwalKelasDb extends JpaRepository <JadwalKelasModel, Long> {
    List<JadwalKelasModel> findAllByPengajarKelas(PengajarModel pengajarKelas);
}
