package com.projectpop.quanta.pengajar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.repository.PengajarDb;

import java.util.ArrayList;
import java.util.List;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.repository.MataPelajaranDb;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;

import static com.projectpop.quanta.user.auth.PasswordManager.encrypt;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Transactional
public class PengajarServiceImpl implements PengajarService {
    @Autowired
    PengajarDb pengajarDb;
    
    @Autowired
    MataPelajaranDb mataPelajaranDb;

    @Override
    public List<PengajarModel> getListPengajarActive() {
        List<PengajarModel> listPengajarActive = new ArrayList<>();
        for (PengajarModel pengajar : pengajarDb.findAll()) {
            if (pengajar.getIsActive()) {
                listPengajarActive.add(pengajar);
            }
        }
        return listPengajarActive;
    }

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

        if (result.length() != 0){
            result = result.substring(0, result.length()-2);
        } else {
            result = "-";
        }

        return result;
        
    }

    @Override
    public String getKelasAsuh(PengajarModel pengajar){
        String result = "";
        for (KelasModel kelas: pengajar.getListKelasAsuh()) {
            if(kelas.getTahunAjar().getIsAktif()){
                result = result + kelas.getName() + ", ";
            }
        }

        if (result.length() != 0){
            result = result.substring(0, result.length()-2);
        } else {
            result = "-";
        }
        
        return result;
    }

    @Override
    public PengajarModel getPengajarById(Integer id) {
        Optional<PengajarModel> pengajar = pengajarDb.findById(id);
        if(pengajar.isPresent()) {
            return pengajar.get();
        } else return null;
    }

    @Override
    public PengajarModel inactivePengajar(PengajarModel pengajar) {
        pengajar.setIsActive(false);
        pengajarDb.save(pengajar);
        return pengajar;
    }

    @Override
    public PengajarModel activePengajar(PengajarModel pengajar) {
        pengajar.setIsActive(true);
        pengajarDb.save(pengajar);
        return pengajar;
    }

    @Override
    public PengajarModel updatePengajar(PengajarModel pengajar) {
        pengajarDb.save(pengajar);
        return pengajar;
    }

    @Override
    public int getNumberOfKelasAktif(PengajarModel pengajar) {
        int result = 0;
        for (JadwalKelasModel jadwalKelas: pengajar.getListJadwalKelas()) {
            if(jadwalKelas.getEndDateClass().isAfter(LocalDateTime.now())){
                result++;
            }
        }

        return result;
    }

    @Override
    public int getNumberOfKonsultasiAktif(PengajarModel pengajar) {
        int result = 0;
        for (KonsultasiModel konsultasi: pengajar.getListKonsultasiPengajar()) {
            if(konsultasi.getEndTime().isAfter(LocalDateTime.now())){
                result++;
            }
        }

        return result;
    }

    @Override
    public List<PengajarModel> getListKakakAsuh() {
        Optional<List<PengajarModel>> kakakAsuhList = pengajarDb.findKakakAsuh();
        return kakakAsuhList.orElse(null);
    }
}
