package com.projectpop.quanta.jadwalkelas.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;

import java.time.LocalDate;
import java.util.List;

public interface JadwalKelasService {
    JadwalKelasModel getJadwalKelasById(Integer id);
    List<JadwalKelasModel> getListJadwalKelas();
    List<JadwalKelasModel> getJadwalByPengajarAndTanggal(PengajarModel pengajarModel, LocalDate tanggal);
    void addJadwalKelas(JadwalKelasModel jadwalKelas);
    void updateJadwalKelas(JadwalKelasModel jadwalKelas);
    void deleteJadwalKelas(JadwalKelasModel jadwalKelas);
    List<JadwalKelasModel> getListJadwalKelasByIdPengajar(Integer idPengajar);
    List<JadwalKelasModel> getListJadwalKelasByKelas(KelasModel kelas);
    List<JadwalKelasModel> getListJadwalKelasByTahunAjarAndMonth(TahunAjarModel tahunAjar, Integer month);
    List<JadwalKelasModel> getListJadwalKelasByKelasAndTanggal(LocalDate tanggal, KelasModel kelas);
}
