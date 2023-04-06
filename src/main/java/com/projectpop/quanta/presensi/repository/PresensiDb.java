package com.projectpop.quanta.presensi.repository;

import com.projectpop.quanta.presensi.model.PresensiModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresensiDb extends JpaRepository<PresensiModel, String> {
    PresensiModel findById(Integer id);

}
