package com.projectpop.quanta.pengajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.pengajar.model.PengajarModel;

import java.util.Optional;

@Repository
public interface PengajarDb extends JpaRepository<PengajarModel, Long> {
    Optional<PengajarModel> findByEmail(String email);

    Optional<PengajarModel> findById(int id);
}
