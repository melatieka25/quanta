package com.projectpop.quanta.jadwalkelas.repository;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


@Repository
public interface JadwalKelasDb extends JpaRepository<JadwalKelasModel, String>{
    JadwalKelasModel findById(Integer id);
    List<JadwalKelasModel> findAllByPengajarKelas(PengajarModel pengajar);
}
