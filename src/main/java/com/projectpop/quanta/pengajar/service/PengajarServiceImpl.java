package com.projectpop.quanta.pengajar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.repository.MataPelajaranDb;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.repository.PengajarDb;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;

import static com.projectpop.quanta.user.auth.PasswordManager.encrypt;

import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class PengajarServiceImpl implements PengajarService {
    @Autowired
    PengajarDb pengajarDb;

    @Autowired
    MataPelajaranDb mataPelajaranDb;

    @Override
    public void addPengajar(PengajarModel pengajar) {
        String pass = encrypt(pengajar.getPassword());
        pengajar.setPassword(pass);
        pengajarDb.save(pengajar);
    }

    @Override
    public List<PengajarModel> getListPengajar() {
        return pengajarDb.findAll();
    }

    @Override
    public String getPengajarMapel(PengajarModel pengajar){
        String result = "";
        for (PengajarMapelModel pengajarMapelModel: pengajar.getListPengajarMapel()) {
            MataPelajaranModel mapel = pengajarMapelModel.getMapel();
            result = result + mapel.getAbbr() + ", ";
        }

        System.out.println(result);

        result = result.substring(0, result.length()-2);
        return result;
        // return "test";
    }
}
