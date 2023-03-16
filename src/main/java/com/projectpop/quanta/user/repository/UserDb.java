package com.projectpop.quanta.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.user.model.UserModel;

import java.util.Optional;

@Repository
public interface UserDb extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByName(String name);

    Optional<UserModel> findByEmail(String email);

}