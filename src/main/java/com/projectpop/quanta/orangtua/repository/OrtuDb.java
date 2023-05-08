package com.projectpop.quanta.orangtua.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.orangtua.model.OrtuModel;

import java.util.Optional;

@Repository
public interface OrtuDb extends JpaRepository<OrtuModel, Long> {
    Optional<OrtuModel> findByEmail(String email);


    Optional<OrtuModel> findById(int id);
}