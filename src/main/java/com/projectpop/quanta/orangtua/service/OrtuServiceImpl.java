package com.projectpop.quanta.orangtua.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.repository.OrtuDb;
import static com.projectpop.quanta.user.auth.PasswordManager.encrypt;
import javax.transaction.Transactional;

@Service
@Transactional
public class OrtuServiceImpl implements OrtuService {

    @Autowired
    OrtuDb ortuDb;

    @Override
    public List<OrtuModel> getListOrtu(){
        return ortuDb.findAll();
    }

    @Override
    public void addOrtu(OrtuModel ortu) {
        String pass = encrypt(ortu.getPassword());
        ortu.setPassword(pass);
        ortuDb.save(ortu);
    }
    
}
