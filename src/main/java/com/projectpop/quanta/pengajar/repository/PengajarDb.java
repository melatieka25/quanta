package com.projectpop.quanta.pengajar.repository;

import com.projectpop.quanta.pengajar.model.PengajarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PengajarDb extends JpaRepository<PengajarModel, String> {
    @Query("SELECT p FROM PengajarModel p WHERE p.isKakakAsuh = true ")
    Optional<List<PengajarModel>> findKakakAsuh();

    Optional<PengajarModel> findByEmail(String email);

    Optional<PengajarModel> findById(int id);
}
