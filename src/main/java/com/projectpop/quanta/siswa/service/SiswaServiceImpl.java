package com.projectpop.quanta.siswa.service;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.siswajadwalkelas.model.SiswaJadwalModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.siswa.model.Jenjang;
import com.projectpop.quanta.siswa.model.SiswaCsvModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.repository.SiswaDb;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.user.model.Gender;
import com.projectpop.quanta.user.model.Religion;
import com.projectpop.quanta.user.model.UserRole;

import static com.projectpop.quanta.user.auth.PasswordManager.encrypt;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class SiswaServiceImpl implements SiswaService {
    @Autowired
    private SiswaDb siswaDb;

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

    @Override
    public SiswaModel findSiswaByEmail(String email) {
        Optional<SiswaModel> siswa = siswaDb.findByEmail(email);
        if(siswa.isPresent()) {
            return siswa.get();
        } else return null;
    }

    @Override
    public List<SiswaModel> getListSiswaExsAndNoClass(List<SiswaModel> listSiswa, KelasModel kelas) {
        List<SiswaModel> listSiswa2 =  listSiswa;

        for (int i = 0; i < listSiswa2.size(); i++){
            SiswaModel siswaItr = listSiswa2.get(i);
            if (getKelasBimbel(siswaItr) != null){
                if (getKelasBimbel(siswaItr).getId() != kelas.getId()){
                    listSiswa2.remove(siswaItr);
                    i--;
                }
            }
        }

        return listSiswa2;
    }

    @Override
    public List<SiswaModel> getListSiswaActive() {
        List<SiswaModel> listSiswaAktif = new ArrayList<SiswaModel>();
        List<SiswaModel> listSiswa = getListSiswa();
        for (SiswaModel siswa : listSiswa){
            if(siswa.getIsActive()){
                listSiswaAktif.add(siswa);
            }
            else{
                return null;
            }
        }
        return listSiswaAktif;
    }
    @Override
    public SiswaModel getSiswaByEmail(String email) {
        Optional<SiswaModel> siswa = siswaDb.findByEmail(email);
        if(siswa.isPresent()) {
            return siswa.get();
        } else return null;
    }

    @Override
    public SiswaModel convertSiswaCsv(SiswaCsvModel siswaCsv) {
        SiswaModel siswa = new SiswaModel();
        siswa.setName(siswaCsv.getFullName());
        siswa.setAddress(siswaCsv.getAddress());
        siswa.setNickname(siswaCsv.getNickname());
        siswa.setPhone_num(siswaCsv.getPhone_num());
        siswa.setGender(Gender.valueOf(siswaCsv.getGender()));
        siswa.setEmail(siswaCsv.getEmail());
        siswa.setRole(UserRole.valueOf("SISWA"));
        siswa.setIsPassUpdated(false);
        siswa.setPob(siswaCsv.getPob());
        siswa.setDob(siswaCsv.getDob());
        siswa.setReligion(Religion.valueOf(siswaCsv.getReligion()));
        siswa.setIsActive(true);
        siswa.setSekolah(siswaCsv.getSekolah());
        siswa.setJenjang(Jenjang.valueOf(siswaCsv.getJenjang()));

        return siswa;
    }
}
