package com.projectpop.quanta.konsultasi.service;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.Jenjang;

import java.time.LocalDate;
import java.util.List;

public interface KonsultasiService {

    List<KonsultasiModel> getListKonsultasi();

    KonsultasiModel getKonsultasi(Integer id);

    List<KonsultasiModel> getListMyKonsultasiPengajar(PengajarModel pengajar);

    List<KonsultasiModel> getListMyRequestKonsultasi(PengajarModel pengajar);

    List<KonsultasiModel> getListKonsultasiByJenjang(Jenjang jenjang);

    List<KonsultasiModel> getListKonsultasiByPengajarAndTanggal(PengajarModel pengajar, LocalDate tanggal);

    KonsultasiModel createKonsultasi(KonsultasiModel konsultasiModel);


}
