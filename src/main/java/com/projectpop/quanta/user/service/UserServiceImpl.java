package com.projectpop.quanta.user.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.repository.UserDb;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserDb userDb;

    @Override
    public UserModel getUserByEmail(String email) {
        Optional<UserModel> user = userDb.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else return null;
    }

    @Override
    public UserModel getUserById(Integer id) {
        Optional<UserModel> user = userDb.findById(id);
        if (user.isPresent()){
            return user.get();
        }
        else {
            throw new NoSuchElementException();
        }
    }


}
