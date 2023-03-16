package com.projectpop.quanta.orangtua.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.repository.OrtuDb;
import com.projectpop.quanta.siswa.model.SiswaModel;

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

    @Override
    public OrtuModel getOrtuById(int id) {
        Optional<OrtuModel> ortu = ortuDb.findById(id);
        if(ortu.isPresent()) {
            return ortu.get();
        } else return null;
    }

    @Override
    public String getAnakAktif(OrtuModel ortu){
        String result = "";
        for (SiswaModel siswa: ortu.getListAnak()) {
            if(siswa.getIsActive()){
                result = result + siswa.getName() + ", ";
            }
        }

        if (result.length() != 0){
            result = result.substring(0, result.length()-2);
        } else {
            result = "-";
        }
        
        return result;
        // return "test";
    }

    @Override
    public OrtuModel inactiveOrtu(OrtuModel ortu) {
        ortu.setIsActive(false);
        ortuDb.save(ortu);
        return ortu;
    }

    @Override
    public OrtuModel activeOrtu(OrtuModel ortu) {
        ortu.setIsActive(true);
        ortuDb.save(ortu);
        return ortu;
    }

    @Override
    public OrtuModel updateOrtu(OrtuModel ortu) {
        ortuDb.save(ortu);
        return ortu;
    }
    
}
