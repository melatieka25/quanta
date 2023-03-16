package com.projectpop.quanta.jadwalkelas.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.repository.JadwalKelasDb;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.repository.PengajarDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class JadwalKelasServiceImpl implements JadwalKelasService{
    @Autowired
    JadwalKelasDb jadwalKelasDb;

    @Autowired
    PengajarDb pengajarDb;

    @Override
    public List<JadwalKelasModel> getListJadwalKelasByIdPengajar(Integer idPengajar) {
        PengajarModel pengajarKelas = pengajarDb.findPengajarModelById(idPengajar);
        return jadwalKelasDb.findAllByPengajarKelas(pengajarKelas);
    }

    @Override
    public List<JadwalKelasModel> getAllListJadwalKelas() {
        return jadwalKelasDb.findAll();
    }
}
