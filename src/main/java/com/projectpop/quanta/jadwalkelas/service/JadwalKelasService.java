package com.projectpop.quanta.jadwalkelas.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import java.util.List;

public interface JadwalKelasService {
    JadwalKelasModel getJadwalKelasById(Integer id);
    List<JadwalKelasModel> getListJadwalKelas();
    //boolean cekOngoingJadwal(JadwalKelasModel jadwalKelas);
    void addJadwalKelas(JadwalKelasModel jadwalKelas);
    void updateJadwalKelas(JadwalKelasModel jadwalKelas);
    void deleteJadwalKelas(JadwalKelasModel jadwalKelas);
    List<JadwalKelasModel> getListJadwalKelasByIdPengajar(Integer idPengajar);
}
