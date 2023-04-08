package com.projectpop.quanta.mapel.repository;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MataPelajaranDb extends JpaRepository<MataPelajaranModel, String> {

    Optional<MataPelajaranModel> findById(Integer id);
}
