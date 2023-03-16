package com.projectpop.quanta.pengajar.repository;

import com.projectpop.quanta.pengajar.model.PengajarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PengajarDb extends JpaRepository <PengajarModel, Long>{
    PengajarModel findPengajarModelById(Integer id);
}
