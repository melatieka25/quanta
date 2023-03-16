package com.projectpop.quanta.jadwalkelas.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;

import java.util.List;

public interface JadwalKelasService {
    List<JadwalKelasModel> getListJadwalKelasByIdPengajar(Integer idPengajar);
    List<JadwalKelasModel> getAllListJadwalKelas();
}
