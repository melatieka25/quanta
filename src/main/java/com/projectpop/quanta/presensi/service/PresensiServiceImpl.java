package com.projectpop.quanta.presensi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.presensi.model.PresensiStatus;
import com.projectpop.quanta.presensi.repository.PresensiDb;

@Service
@Transactional
public class PresensiServiceImpl implements PresensiService {
    @Autowired
    PresensiDb presensiDb;

    @Override
    public PresensiModel createPresensi(JadwalKelasModel jadwal) {
        PresensiModel presensi = new PresensiModel();
        presensi.setJadwal(jadwal);
        presensi.setStatus(PresensiStatus.ALPHA);
        return presensi;
    }
    
}
