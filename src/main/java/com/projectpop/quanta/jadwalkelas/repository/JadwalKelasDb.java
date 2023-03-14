package com.projectpop.quanta.jadwalkelas.repository;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface JadwalKelasDb extends JpaRepository<JadwalKelasModel, String>{
    JadwalKelasModel findById(Integer id);
}
