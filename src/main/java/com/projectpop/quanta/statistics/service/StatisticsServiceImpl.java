package com.projectpop.quanta.statistics.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.repository.JadwalKelasDb;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.repository.KelasDb;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.statistics.model.StatsModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import com.projectpop.quanta.tahunajar.repository.TahunAjarDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StatisticsServiceImpl implements StatisticsService{
    @Autowired
    private TahunAjarDb tahunAjarDb;

    @Autowired
    private KelasDb kelasDb;

    @Autowired
    private JadwalKelasDb jadwalKelasDb;

    @Qualifier("jadwalKelasServiceImpl")
    @Autowired
    private JadwalKelasService jadwalKelasService;


    @Override
    public <E> void addJumlahPresensi(E[] listDataVariant, int[] listPresensiHadirJenjang, int[] listAllPresensiJenjang, JadwalKelasModel jadwalKelas, String variant) {
        int idxListElement = 0;
        if (variant.equals("Mapel")){
            String mapelItr = jadwalKelas.getMapelJadwal().getName();
            idxListElement = java.util.Arrays.asList(listDataVariant).indexOf(mapelItr);
        } else {
            String kelasItr = jadwalKelas.getKelas().getName();
            idxListElement = java.util.Arrays.asList(listDataVariant).indexOf(kelasItr);
        }

        int totalHadir = 0;
        int totalPresensi = 0;
        List<PresensiModel> listPresensiHadir= jadwalKelas.getListPresensi();
        for (PresensiModel presensi : listPresensiHadir){
            if (presensi.getStatus().name().equals("HADIR")){
                totalHadir+=1;
            }
            totalPresensi +=1;
        }
        listAllPresensiJenjang[idxListElement] = listAllPresensiJenjang[idxListElement] + totalPresensi;
        listPresensiHadirJenjang[idxListElement] = listPresensiHadirJenjang[idxListElement] + totalHadir;
    }

    @Override
    public void calculatePercentage(int[] listNumerator, int[] listDenumerator, float[] listPercentage) {
        float percentage = 0f;

        for (int i = 0; i < listNumerator.length; i++){
            if (listDenumerator[i] == 0) {
                percentage = 0f;
            } else {
                percentage = (float) listNumerator[i] * 100 / listDenumerator[i];
            }
            listPercentage[i] = percentage;
        }
    }

    @Override
    public void initializeListWithZero(int[] listInitiatedZero) {
        for (int i = 0; i < listInitiatedZero.length; i++){
            listInitiatedZero[i] = 0;
        }
    }

    @Override
    public <E> void getListNamaObject(E[] listObject, String[] listNamaObject) {
        int counter = 0;
        for (E obj: listObject){
            if (obj.getClass() == MataPelajaranModel.class){
                MataPelajaranModel mapel = (MataPelajaranModel) obj;
                listNamaObject[counter++] = (mapel.getName());
            } else if (obj.getClass() == KelasModel.class){
                KelasModel kelas = (KelasModel) obj;
                listNamaObject[counter++] = (kelas.getName());
            }
        }
    }


    @Override
    public <E> String[][] getTargetLabel(E[] listTargetSMP, E[] listTargetSMA) {
        String[][] listNamaTarget = new String[2][];
        listNamaTarget[0] = new String[listTargetSMP.length];
        listNamaTarget[1] = new String[listTargetSMA.length];

        getListNamaObject(listTargetSMP, listNamaTarget[0]);
        getListNamaObject(listTargetSMA, listNamaTarget[1]);
        return listNamaTarget;
    }

    @Override
    public Map<String, String> getKategoriTer(String[][] listNamaKategori, float[][] listPersentasePresensiMapel,TahunAjarModel tahunAjar, Integer month, String tipe) {
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasDb.findAllByTahunAjarAndMonth(tahunAjar, month);


        StatsModel[][] copyListPersentasePresensiMapel = new StatsModel[2][];
        copyListPersentasePresensiMapel[0] = new StatsModel[listNamaKategori[0].length];
        copyListPersentasePresensiMapel[1] = new StatsModel[listNamaKategori[1].length];
        Map<String, String> mapMapel = new HashMap<>();


        for (JadwalKelasModel jadwalKelas : listJadwalKelas){
            if(jadwalKelas.getKelas().getTahunAjar().equals(tahunAjar) && jadwalKelas.getStartDateClass().getMonthValue() == month){
                for (int i =0; i < listPersentasePresensiMapel.length; i++){
                    for (int j=0; j < listPersentasePresensiMapel[i].length; j++){
                        StatsModel newModel = new StatsModel(listNamaKategori[i][j], listPersentasePresensiMapel[i][j]);
                        copyListPersentasePresensiMapel[i][j] = newModel;
                    }
                }

                Arrays.sort(copyListPersentasePresensiMapel[0]);
                Arrays.sort(copyListPersentasePresensiMapel[1]);

                String SMPLowest = copyListPersentasePresensiMapel[0][0].getName();
                String SMPHighest = copyListPersentasePresensiMapel[0][copyListPersentasePresensiMapel[0].length-1].getName();
                String SMALowest = copyListPersentasePresensiMapel[1][0].getName();
                String SMAHighest = copyListPersentasePresensiMapel[1][copyListPersentasePresensiMapel[1].length-1].getName();

                mapMapel.put("Presensi " + tipe + " SMP Tertinggi", SMPHighest);
                mapMapel.put("Presensi " + tipe + " SMP Terendah", SMPLowest);
                mapMapel.put("Presensi " + tipe + " SMA Tertinggi", SMAHighest);
                mapMapel.put("Presensi " + tipe + " SMA Terendah", SMALowest);
            }
        }

        return mapMapel;
    }

    @Override
    public float[][] getKelasStats(TahunAjarModel tahunAjar, Integer month, List<KelasModel> listKelasSMP,  List<KelasModel> listKelasSMA, String[][] listNamaKelas) {
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasDb.findAllByTahunAjarAndMonth(tahunAjar, month);

        int[][] listAllPresensi = new int[2][];
        listAllPresensi[0] = new int[listKelasSMP.size()];
        listAllPresensi[1] = new int[listKelasSMA.size()];

        int[][] listPresensiHadir = new int[2][];
        listPresensiHadir[0] = new int[listKelasSMP.size()];
        listPresensiHadir[1] = new int[listKelasSMA.size()];

        float[][] listPersentaseHadir = new float[2][];
        listPersentaseHadir[0] = new float[listKelasSMP.size()];
        listPersentaseHadir[1] = new float[listKelasSMA.size()];


        for (JadwalKelasModel jadwalKelas : listJadwalKelas){
            if(jadwalKelas.getKelas().getTahunAjar().equals(tahunAjar) && jadwalKelas.getStartDateClass().getMonthValue() == month){
                if (jadwalKelas.getKelas().getJenjang().getDisplayValue().contains("SMP")){
                    addJumlahPresensi(listNamaKelas[0], listPresensiHadir[0], listAllPresensi[0], jadwalKelas, "Kelas");
                } else {
                    addJumlahPresensi(listNamaKelas[1], listPresensiHadir[1], listAllPresensi[1], jadwalKelas, "Kelas");

                }
            }
        }

        calculatePercentage(listPresensiHadir[0], listAllPresensi[0], listPersentaseHadir[0]);
        calculatePercentage(listPresensiHadir[1], listAllPresensi[1], listPersentaseHadir[1]);

        return listPersentaseHadir;
    }

    @Override
    public float[][] getMapelStats(TahunAjarModel tahunAjar, Integer month, List<MataPelajaranModel> listMapelSMP,  List<MataPelajaranModel> listMapelSMA, String[][] listNamaMapel) {
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasDb.findAllByTahunAjarAndMonth(tahunAjar, month);

        int[][] listAllPresensi = new int[2][];
        listAllPresensi[0] = new int[listMapelSMP.size()];
        listAllPresensi[1] = new int[listMapelSMA.size()];

        int[][] listPresensiHadir = new int[2][];
        listPresensiHadir[0] = new int[listMapelSMP.size()];
        listPresensiHadir[1] = new int[listMapelSMA.size()];

        float[][] listPersentaseHadir = new float[2][];
        listPersentaseHadir[0] = new float[listMapelSMP.size()];
        listPersentaseHadir[1] = new float[listMapelSMA.size()];


        for (JadwalKelasModel jadwalKelas : listJadwalKelas){
            if(jadwalKelas.getKelas().getTahunAjar().equals(tahunAjar) && jadwalKelas.getStartDateClass().getMonthValue() == month){
                if (jadwalKelas.getKelas().getJenjang().getDisplayValue().contains("SMP")){
                    addJumlahPresensi(listNamaMapel[0], listPresensiHadir[0], listAllPresensi[0], jadwalKelas, "Mapel");
                } else {
                    addJumlahPresensi(listNamaMapel[1], listPresensiHadir[1], listAllPresensi[1], jadwalKelas, "Mapel");
                }
            }
        }

        calculatePercentage(listPresensiHadir[0], listAllPresensi[0], listPersentaseHadir[0]);
        calculatePercentage(listPresensiHadir[1], listAllPresensi[1], listPersentaseHadir[1]);

        return listPersentaseHadir;
    }
}
