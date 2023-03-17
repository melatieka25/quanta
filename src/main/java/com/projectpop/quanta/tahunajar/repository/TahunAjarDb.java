package com.projectpop.quanta.tahunajar.repository;

import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TahunAjarDb extends JpaRepository<TahunAjarModel, String> {
}
