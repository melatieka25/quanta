package com.projectpop.quanta.kelas.repository;

import com.projectpop.quanta.kelas.model.KelasModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KelasDb extends JpaRepository<KelasModel,Long> {
}
