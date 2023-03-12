package com.projectpop.quanta.siswa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.repository.SiswaDb;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;

import static com.projectpop.quanta.user.auth.PasswordManager.encrypt;

import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class SiswaServiceImpl implements SiswaService {
    @Autowired
    SiswaDb siswaDb;

    @Override
    public void addSiswa(SiswaModel siswa) {
        String pass = encrypt(siswa.getPassword());
        siswa.setPassword(pass);
        siswaDb.save(siswa);
    }

    @Override
    public List<SiswaModel> getListSiswa() {
        return siswaDb.findAll();
    }

    @Override
    public String getKelasBimbel(SiswaModel siswa){
        String result = "";
        for (SiswaKelasModel siswaKelasModel: siswa.getListKelasSiswa()) {
            KelasModel kelas = siswaKelasModel.getKelasSiswa();
            if (kelas.getTahunAjar().getIsAktif()) {
                result = kelas.getName();
                break;
            }
        }
        return result;
    }
}
