package com.projectpop.quanta.mapel.repository;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MataPelajaranDb extends JpaRepository<MataPelajaranModel, String> {

    Optional<MataPelajaranModel> findById(Integer id);
    Optional<List<MataPelajaranModel>> findByIsSMAIsTrue();
    Optional<List<MataPelajaranModel>> findByIsSMPIsTrue();
    @Query("SELECT m.name FROM MataPelajaranModel m where m.isSMA = true")
    Optional<List<String>> findAllNameByIsSMAIsTrue();

    @Query("SELECT m.name FROM MataPelajaranModel m where m.isSMP = true")
    Optional<List<String>> findAllNameByIsSMPIsTrue();

    @Query("SELECT name FROM MataPelajaranModel")
    Optional<List<String>> findAllName();
}
