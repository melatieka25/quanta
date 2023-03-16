package com.projectpop.quanta.user.service;

import com.projectpop.quanta.user.model.UserModel;

public interface UserService {

    UserModel getUserByEmail(String email);
    UserModel getUserById(Integer id);
//
//    UserModel addUser(UserModel user);
//
//    String encrypt(String password);
}
