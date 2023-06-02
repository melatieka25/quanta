package com.projectpop.quanta.konsultasi.service;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.model.StatusKonsul;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.Jenjang;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.siswa.model.SiswaModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public interface KonsultasiService {

    List<KonsultasiModel> getListKonsultasiHariIni();

    KonsultasiModel getKonsultasi(Integer id);

    List<KonsultasiModel> getListMyKonsultasiPengajar(PengajarModel pengajar);

    List<KonsultasiModel> getListMyKonsultasiPengajarAndStatus(PengajarModel pengajar, StatusKonsul status);

    List<KonsultasiModel> getListKonsultasiByJenjangAndStatus(Jenjang jenjang, StatusKonsul status);

    List<KonsultasiModel> getListKonsultasiByPengajarAndTanggal(PengajarModel pengajar, LocalDate tanggal);

    KonsultasiModel createKonsultasi(KonsultasiModel konsultasiModel);

    List<KonsultasiModel> getListKonsultasiByUser(UserModel user);

    void reloadStatus();

    List<KonsultasiModel> getListKonsultasiStatus(StatusKonsul status);
    
    List<KonsultasiModel> getListKonsultasiByJenjangHariIni(Jenjang jenjang);

    List<KonsultasiModel> getListKonsultasiByPengajarAndStatusAndTanggal(PengajarModel pengajar, StatusKonsul satus, LocalDate tanggal);

    KonsultasiModel updateKonsultasi(KonsultasiModel konsultasi);

    boolean getIsSiswaAvailable(SiswaModel siswa, KonsultasiModel konsultasi);

    ArrayList<LocalTime> getNotAvailableWaktuKonsulPengajar(PengajarModel pengajar, LocalDate tanggal);

    boolean getIsPengajarAvailable(PengajarModel pengajar, KonsultasiModel konsultasi);

    List<LocalTime> getListWaktuAwalKonsultasi(LocalDate tanggal);

    boolean isInRangeTimeExtend(LocalDateTime waktuAwalKonsul, LocalDateTime waktuAkirKonsulExtend);

    List<KonsultasiModel> getRekomendasiKonsultasi(SiswaModel siswa, Jenjang jenjang);

    List<KonsultasiModel> getListKonsultasi();

    boolean isExtendAble(KonsultasiModel konsultasi);

    boolean isToClose(KonsultasiModel konsultasi);

    void tolakKonsultasiOtomatis(KonsultasiModel konsultasi);

    List<KonsultasiModel> getRequestKonsultasi(PengajarModel Pengajar);

    void reloadRequestKonsultasi();

    List<KonsultasiModel> getListKonsultasiPengajarHariIni(PengajarModel pengajar);
    List<KonsultasiModel> getListKonsultasiByTahunAjarAndMonth(TahunAjarModel tahunAjar, Integer month);
    List<KonsultasiModel> getListKonsultasiByTahunAjar(TahunAjarModel tahunAjar);

}
