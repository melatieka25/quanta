package com.projectpop.quanta.siswa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.siswa.model.SiswaModel;

import java.util.Optional;

@Repository
public interface SiswaDb extends JpaRepository<SiswaModel, Long> {
    Optional<SiswaModel> findByEmail(String email);

    Optional<SiswaModel> findById(int id);
}
