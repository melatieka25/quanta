package com.projectpop.quanta.siswa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.repository.SiswaDb;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;

import static com.projectpop.quanta.user.auth.PasswordManager.encrypt;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


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
    public KelasModel getKelasBimbel(SiswaModel siswa){
        KelasModel result = null;
        for (SiswaKelasModel siswaKelasModel: siswa.getListKelasSiswa()) {
            KelasModel kelas = siswaKelasModel.getKelasSiswa();
            if (kelas.getTahunAjar().getIsAktif()) {
                result = kelas;
                break;
            }
        }
        return result;
    }

    @Override
    public SiswaModel getSiswaById(int id) {
        Optional<SiswaModel> siswa = siswaDb.findById(id);
        if(siswa.isPresent()) {
            return siswa.get();
        } else return null;
    }

    @Override
    public SiswaModel inactiveSiswa(SiswaModel siswa) {
        siswa.setIsActive(false);
        siswaDb.save(siswa);
        return siswa;
    }

    @Override
    public SiswaModel activeSiswa(SiswaModel siswa) {
        siswa.setIsActive(true);
        siswaDb.save(siswa);
        return siswa;
    }

    @Override
    public SiswaModel updateSiswa(SiswaModel siswa) {
        siswaDb.save(siswa);
        return siswa;
    }

    @Override
    public int getNumberOfKonsultasiAktif(SiswaModel siswa) {
        int result = 0;
        for (SiswaKonsultasiModel siswaKonsultasi: siswa.getListKonsultasiSiswa()) {
            if(siswaKonsultasi.getKonsultasi().getEndTime().isAfter(LocalDateTime.now())){
                result++;
            }
        }

        return result;
    }
}
