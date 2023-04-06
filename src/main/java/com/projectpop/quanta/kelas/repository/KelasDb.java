package com.projectpop.quanta.kelas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.kelas.model.JadwalAvail;
import com.projectpop.quanta.kelas.model.KelasModel;
import java.util.List;
import java.util.Optional;

@Repository
public interface KelasDb extends JpaRepository<KelasModel, String> {
    List<KelasModel> findByDays(JadwalAvail days);
    Optional<KelasModel> findById(Integer id);
    List<KelasModel> findByName(String name);
}
