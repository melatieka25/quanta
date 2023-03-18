package com.projectpop.quanta.siswa.repository;

import com.projectpop.quanta.siswa.model.SiswaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiswaDb extends JpaRepository<SiswaModel, String> {
    Optional<SiswaModel> findById(Integer id);
}
