package com.projectpop.quanta.presensi.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.presensi.model.PresensiModel;

import java.util.List;

public interface PresensiService {
    PresensiModel getPresensiModelById(Integer id);
    List<PresensiModel> getListPresensi();
    PresensiModel updatePresensi(PresensiModel presensiModel);
    PresensiModel createPresensi(JadwalKelasModel jadwal);
}
