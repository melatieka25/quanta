package com.projectpop.quanta.mapel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;

import java.util.Optional;

@Repository
public interface MataPelajaranDb extends JpaRepository<MataPelajaranModel, Long> {
    Optional<MataPelajaranModel> findById(int id);
}
