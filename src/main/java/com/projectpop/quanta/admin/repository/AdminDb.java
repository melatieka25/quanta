package com.projectpop.quanta.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.admin.model.AdminModel;

import java.util.Optional;

@Repository
public interface AdminDb extends JpaRepository<AdminModel, String> {

    Optional<AdminModel> findByEmail(String email);

    Optional<AdminModel> findById(Integer id);
}
