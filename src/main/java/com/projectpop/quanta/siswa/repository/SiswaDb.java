package com.projectpop.quanta.siswa.repository;

import com.projectpop.quanta.siswa.model.SiswaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiswaDb extends JpaRepository<SiswaModel, String> { }
