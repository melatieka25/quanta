package com.projectpop.quanta.pengajar.service;

import com.projectpop.quanta.user.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.pengajar.model.Education;
import com.projectpop.quanta.pengajar.model.PengajarCsvModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.model.StatusPernikahan;
import com.projectpop.quanta.pengajar.repository.PengajarDb;

import java.util.ArrayList;
import java.util.List;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.repository.MataPelajaranDb;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import org.springframework.ui.Model;
import com.projectpop.quanta.user.model.Gender;
import com.projectpop.quanta.user.model.Religion;
import com.projectpop.quanta.user.model.UserRole;

import static com.projectpop.quanta.user.auth.PasswordManager.encrypt;

import javax.transaction.Transactional;

import java.time.LocalDate;
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
        Optional<PengajarModel> pengajar = pengajarDb.findPengajarModelById(id);
        if(pengajar.isPresent()) {
            return pengajar.get();
        } else{
            return null;
        }
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
    public PengajarModel getPengajarByEmail(String email) {
        Optional<PengajarModel> pengajar = pengajarDb.findByEmail(email);
        if(pengajar.isPresent()) {
            return pengajar.get();
        } else return null;
    }

    

    public List<PengajarModel> getListKakakAsuh() {
        Optional<List<PengajarModel>> kakakAsuhList = pengajarDb.findKakakAsuh();
        return kakakAsuhList.orElse(null);
    }

    @Override
    public void checkIsPengajarDanKakakAsuh(UserModel userModel, Model model) {
        PengajarModel pengajarModel = getPengajarById(userModel.getId());
        if (null != pengajarModel){
            model.addAttribute("isKakakAsuh", pengajarModel.getIsKakakAsuh());
        }
    }
    @Override
    public List<KelasModel> getListKelasAsuh(PengajarModel pengajar){
        List<KelasModel> kelasAsuhAktif = new ArrayList<>();
        for (KelasModel kelas: pengajar.getListKelasAsuh()) {
            if(kelas.getTahunAjar().getIsAktif()){
                kelasAsuhAktif.add(kelas);
            }
        }
        return kelasAsuhAktif;
    }

    @Override
    public PengajarModel convertPengajarCsv(PengajarCsvModel pengajarCsv) {
        PengajarModel pengajar = new PengajarModel();
        pengajar.setName(pengajarCsv.getFullName());
        pengajar.setAddress(pengajarCsv.getAddress());
        pengajar.setNickname(pengajarCsv.getNickname());
        pengajar.setPhone_num(pengajarCsv.getPhone_num());
        pengajar.setGender(Gender.valueOf(pengajarCsv.getGender()));
        pengajar.setEmail(pengajarCsv.getEmail());
        pengajar.setRole(UserRole.valueOf("PENGAJAR"));
        pengajar.setIsPassUpdated(false);
        pengajar.setPob(pengajarCsv.getPob());
        pengajar.setDob(pengajarCsv.getDob());
        pengajar.setReligion(Religion.valueOf(pengajarCsv.getReligion()));
        pengajar.setIsActive(true);
        pengajar.setKtp(pengajarCsv.getKtp());
        pengajar.setStatus(StatusPernikahan.valueOf(pengajarCsv.getStatus()));
        pengajar.setLastEdu(Education.valueOf(pengajarCsv.getLastEdu()));
        pengajar.setJurusan(pengajarCsv.getJurusan());
        pengajar.setIsKakakAsuh(false);
        pengajar.setStartDate(LocalDate.now());
        pengajar.setUniversity(pengajarCsv.getUniversity());

        return pengajar;
    }
}
