package com.projectpop.quanta.presensi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.presensi.model.PresensiModel;

@Repository
public interface PresensiDb extends JpaRepository<PresensiModel, String> {
    
}
