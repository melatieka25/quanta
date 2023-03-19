package com.projectpop.quanta.presensi.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.presensi.model.PresensiModel;

public interface PresensiService {
    PresensiModel createPresensi(JadwalKelasModel jadwal);
}
