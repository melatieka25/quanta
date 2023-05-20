package com.projectpop.quanta.statistics.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    <E> void addJumlahPresensi(E[] listDataVariant, int[] listPresensiHadirJenjang, int[] listAllPresensiJenjang, JadwalKelasModel jadwalKelas, String variant);
    void calculatePercentage(int[] listNumerator, int[] listDenumerator, float[] listPercentage);
    void  initializeListWithZero(int[] listInitiatedZero);
    <E>void getListNamaObject(E[] listObject, String[] listNamaObject);
    float[][] getMapelStats(TahunAjarModel tahunAjar, Integer month, List<MataPelajaranModel> listMapelSMP,  List<MataPelajaranModel> listMapelSMA, String[][] listNamaMapel);
    float[][] getKelasStats(TahunAjarModel tahunAjar, Integer month,  List<KelasModel> listKelasSMP,  List<KelasModel> listKelasSMA, String[][] listNamaKelas);
    <E> String[][] getTargetLabel(E[] listTargetSMP, E[] listTargetSMA);
    Map<String, String> getKategoriTer(String[][]listNamaMapel, float[][] listPersentasePresensiMapel,TahunAjarModel tahunAjar, Integer month, String tipe);
}
