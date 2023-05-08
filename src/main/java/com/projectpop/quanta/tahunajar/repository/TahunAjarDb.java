package com.projectpop.quanta.tahunajar.repository;

import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TahunAjarDb extends JpaRepository<TahunAjarModel, String> {

    Optional<TahunAjarModel> findByIsAktifIsTrue();
    Optional<TahunAjarModel> findById(int id);
}
