package com.projectpop.quanta.konsultasi.service;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.model.StatusKonsul;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.Jenjang;

import java.time.LocalDate;
import java.util.List;

public interface KonsultasiService {

    List<KonsultasiModel> getListKonsultasiHariIni();

    KonsultasiModel getKonsultasi(Integer id);

    List<KonsultasiModel> getListMyKonsultasiPengajar(PengajarModel pengajar);

    List<KonsultasiModel> getListMyKonsultasiPengajarAndStatus(PengajarModel pengajar, StatusKonsul status);

    List<KonsultasiModel> getListKonsultasiByJenjangAndStatus(Jenjang jenjang, StatusKonsul status);

    List<KonsultasiModel> getListKonsultasiByPengajarAndTanggal(PengajarModel pengajar, LocalDate tanggal);

    KonsultasiModel createKonsultasi(KonsultasiModel konsultasiModel);

    void reloadStatus();

    List<KonsultasiModel> getListKonsultasiStatus(StatusKonsul status);
    
    List<KonsultasiModel> getListKonsultasiByJenjangHariIni(Jenjang jenjang);

    List<KonsultasiModel> getListKonsultasiByPengajarAndStatusAndTanggal(PengajarModel pengajar, StatusKonsul satus, LocalDate tanggal);




}
