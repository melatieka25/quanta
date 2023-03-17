package com.projectpop.quanta.siswakelas.repository;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiswaKelasDb extends JpaRepository<SiswaKelasModel, String> {
    Optional<SiswaKelasModel> findById(Integer id);
}
