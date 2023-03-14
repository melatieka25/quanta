package com.projectpop.quanta.pengajar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.pengajar.model.PengajarModel;

@Repository
public interface PengajarDb extends JpaRepository<PengajarModel, String>{
    PengajarModel findById(Integer id);
}
