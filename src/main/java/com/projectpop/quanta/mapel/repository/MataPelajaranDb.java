package com.projectpop.quanta.mapel.repository;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface MataPelajaranDb extends JpaRepository<MataPelajaranModel, String>{
    MataPelajaranModel findById(Integer id);
}
