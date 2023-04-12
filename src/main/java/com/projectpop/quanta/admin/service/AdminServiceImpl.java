package com.projectpop.quanta.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.admin.model.AdminModel;
import com.projectpop.quanta.admin.repository.AdminDb;

import javax.transaction.Transactional;

import java.util.Optional;


@Service
@Transactional
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminDb adminDb;

    @Override
    public AdminModel getAdminById(int id) {
        Optional<AdminModel> admin = adminDb.findById(id);
        if(admin.isPresent()) {
            return admin.get();
        } else return null;
    }
}
