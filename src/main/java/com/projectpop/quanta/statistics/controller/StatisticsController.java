package com.projectpop.quanta.statistics.controller;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.service.KelasService;
import com.projectpop.quanta.konsultasi.service.KonsultasiService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MapelService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.statistics.model.StatsModel;
import com.projectpop.quanta.statistics.service.KonsulStatsService;
import com.projectpop.quanta.statistics.service.StatisticsService;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import com.projectpop.quanta.tahunajar.service.TahunAjarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/statistik")
public class StatisticsController {
    @Qualifier("mapelServiceImpl")
    @Autowired
    private MapelService mapelService;

    @Qualifier("tahunAjarServiceImpl")
    @Autowired
    private TahunAjarService tahunAjarService;

    @Qualifier("kelasServiceImpl")
    @Autowired
    private KelasService kelasService;

    @Qualifier("konsulStatsServiceImpl")
    @Autowired
    private KonsulStatsService konsulStatsService;

    @Qualifier("statisticsServiceImpl")
    @Autowired
    private StatisticsService statisticsService;

    @Qualifier("pengajarServiceImpl")
    @Autowired
    private PengajarService pengajarService;


    @GetMapping("/mapel")
    public String viewStatisticMapel(Model model, @RequestParam(value = "tahun-ajar", required = false) String tahunAjarId, @RequestParam(value = "month", required = false) Integer month){
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        TahunAjarModel tahunAjar = null;
        LocalDate currentdate = LocalDate.now();
        TahunAjarModel tahunAjarAktif = tahunAjarService.getTahunAjarAktif();

        if (tahunAjarId == null){
            tahunAjar = tahunAjarAktif;
        } else {
            tahunAjar = tahunAjarService.getTahunAjarById(tahunAjarId);
        }

        if (month == null){
            month = currentdate.getMonthValue();
        }

        List<MataPelajaranModel> listMapelSMP= mapelService.getMapelSMP();
        List<MataPelajaranModel> listMapelSMA= mapelService.getMapelSMA();

        String[][] listNamaMapel = statisticsService.getTargetLabel(listMapelSMP.toArray(), listMapelSMA.toArray());
        float[][] listPersentasePresensiMapel = statisticsService.getMapelStats(tahunAjar, month, listMapelSMP, listMapelSMA, listNamaMapel);
        Map<String, String> mapMapel = statisticsService.getKategoriTer(listNamaMapel, listPersentasePresensiMapel, tahunAjar,  month, "Mapel");

        String dataLabelSMP = "Rekap Presensi Mata Pelajaran SMP";
        String dataLabelSMA = "Rekap Presensi Mata Pelajaran SMA";

        model.addAttribute("dataLabelSMP", dataLabelSMP);
        model.addAttribute("dataLabelSMA", dataLabelSMA);
        model.addAttribute("listNamaMapel", listNamaMapel);
        model.addAttribute("listPersentaseMapel", listPersentasePresensiMapel);
        model.addAttribute("tahun-ajar", tahunAjar);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("month", month);
        model.addAttribute("mapMapelTer", mapMapel);
        return "statistik/statistics";
    }
    @GetMapping("/kelas")
    public String viewStatisticKelas(Model model, @RequestParam(value = "tahun-ajar", required = false) String tahunAjarId, @RequestParam(value = "month", required = false) Integer month){
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        TahunAjarModel tahunAjar = null;
        LocalDate currentdate = LocalDate.now();
        TahunAjarModel tahunAjarAktif = tahunAjarService.getTahunAjarAktif();

        if (tahunAjarId == null){
            tahunAjar = tahunAjarAktif;
        } else {
            tahunAjar = tahunAjarService.getTahunAjarById(tahunAjarId);
        }

        if (month == null){
            month = currentdate.getMonthValue();
        }

        List<KelasModel> listKelasSMP = kelasService.getKelasSMP();
        List<KelasModel> listKelasSMA = kelasService.getKelasSMA();

        String[][] listNamaKelas = statisticsService.getTargetLabel(listKelasSMP.toArray(), listKelasSMA.toArray());
        float[][] listPersentasePresensiKelas = statisticsService.getKelasStats(tahunAjar, month, listKelasSMP, listKelasSMA, listNamaKelas);
        Map<String, String> mapKelas = statisticsService.getKategoriTer(listNamaKelas, listPersentasePresensiKelas, tahunAjar,  month, "Kelas");

        String dataLabelSMP = "Rekap Presensi Kelas SMP";
        String dataLabelSMA = "Rekap Presensi Kelas SMA";

        model.addAttribute("dataLabelSMP", dataLabelSMP);
        model.addAttribute("dataLabelSMA", dataLabelSMA);
        model.addAttribute("listNamaKelas", listNamaKelas);
        model.addAttribute("listPersentaseKelas", listPersentasePresensiKelas);
        model.addAttribute("tahun-ajar", tahunAjar);
        model.addAttribute("month", month);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("MapKelasTer", mapKelas);

        return "statistik/statistics-kelas";
    }

    @GetMapping("/konsultasi/mapel")
    public String viewStatisticMapelConsultation(Model model, @RequestParam(value = "tahun-ajar", required = false) String tahunAjarId, @RequestParam(value = "month", required = false) Integer month){
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        TahunAjarModel tahunAjar = null;
        LocalDate currentdate = LocalDate.now();
        TahunAjarModel tahunAjarAktif = tahunAjarService.getTahunAjarAktif();

        if (tahunAjarId == null){
            tahunAjar = tahunAjarAktif;
        } else {
            tahunAjar = tahunAjarService.getTahunAjarById(tahunAjarId);
        }

        if (month == null){
            month = currentdate.getMonthValue();
        }

        // SMP
        List<String> listLabelSMP = mapelService.getMapelSMPName();
        StatsModel[] dataStatsSMP = konsulStatsService.calculateValue(listLabelSMP, tahunAjar, month, "SMP");
        Map<String, String> overviewSMP = konsulStatsService.getOverview(dataStatsSMP, tahunAjar, month, "SMP");

        Integer[] durasiKonsultasiSMP = new Integer[dataStatsSMP.length];

        for (int i=0; i < dataStatsSMP.length; i++){
            durasiKonsultasiSMP[i] = (int) dataStatsSMP[i].getValue();
        }

        // SMA
        List<String> listLabelSMA = mapelService.getMapelSMAName();
        StatsModel[] dataStatsSMA = konsulStatsService.calculateValue(listLabelSMA, tahunAjar, month, "SMA");
        Map<String, String> overviewSMA = konsulStatsService.getOverview(dataStatsSMA, tahunAjar, month, "SMA");

        Integer[] durasiKonsultasiSMA = new Integer[dataStatsSMA.length];

        for (int i=0; i < dataStatsSMA.length; i++){
            durasiKonsultasiSMA[i] = (int) dataStatsSMA[i].getValue();
        }

        // Label Chart
        String dataLabelSMP = "Rekap Konsultasi Mata Pelajaran SMP";
        String dataLabelSMA = "Rekap Konsultasi Mata Pelajaran SMA";

        //SMP
        model.addAttribute("dataLabelSMP", dataLabelSMP);
        model.addAttribute("dataStatsSMP", durasiKonsultasiSMP);
        model.addAttribute("listLabelSMP", listLabelSMP);
        model.addAttribute("overviewSMP", overviewSMP);

        //SMA
        model.addAttribute("dataLabelSMA", dataLabelSMA);
        model.addAttribute("dataStatsSMA", durasiKonsultasiSMA);
        model.addAttribute("listLabelSMA", listLabelSMA);
        model.addAttribute("overviewSMA", overviewSMA);


        model.addAttribute("tahun-ajar", tahunAjar);
        model.addAttribute("month", month);
        model.addAttribute("listTahunAjar", listTahunAjar);

        return "statistik/statistics-konsultasi-mapel";
    }

    @GetMapping("/konsultasi/pengajar")
    public String viewStatisticPengajarConsultation(Model model, @RequestParam(value = "tahun-ajar", required = false) String tahunAjarId, @RequestParam(value = "month", required = false) Integer month){
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        TahunAjarModel tahunAjar = null;
        LocalDate currentdate = LocalDate.now();
        TahunAjarModel tahunAjarAktif = tahunAjarService.getTahunAjarAktif();

        if (tahunAjarId == null){
            tahunAjar = tahunAjarAktif;
        } else {
            tahunAjar = tahunAjarService.getTahunAjarById(tahunAjarId);
        }

        if (month == null){
            month = currentdate.getMonthValue();
        }

        List<PengajarModel> listPengajar = pengajarService.getListPengajarActive();

        List<String> listLabelPengajar = new ArrayList<>();

        for (PengajarModel pengajar : listPengajar){
            listLabelPengajar.add(pengajar.getName());
        }

        //  SMP
        StatsModel[] dataStatsSMP = konsulStatsService.calculateValuePengajar(listLabelPengajar, tahunAjar, month, "SMP");

        Integer[] durasiKonsultasiSMP = new Integer[dataStatsSMP.length];

        for (int i=0; i < dataStatsSMP.length; i++){
            durasiKonsultasiSMP[i] = (int) dataStatsSMP[i].getValue();
        }

        Map<String, String> overViewPengajarSMP = konsulStatsService.getOverview(dataStatsSMP, tahunAjar, month, "SMP");

        // SMA
        StatsModel[] dataStatsSMA = konsulStatsService.calculateValuePengajar(listLabelPengajar, tahunAjar, month, "SMA");

        Integer[] durasiKonsultasiSMA = new Integer[dataStatsSMA.length];

        for (int i=0; i < dataStatsSMA.length; i++){
            durasiKonsultasiSMA[i] = (int) dataStatsSMA[i].getValue();
        }

        Map<String, String> overViewPengajarSMA = konsulStatsService.getOverview(dataStatsSMA, tahunAjar, month, "SMA");

        String dataLabelSMP = "Rekap Konsultasi Pengajar SMP";
        String dataLabelSMA = "Rekap Konsultasi Pengajar SMA";


        //SMP
        model.addAttribute("dataLabelSMP", dataLabelSMP);
        model.addAttribute("dataStatsSMP", durasiKonsultasiSMP);
        model.addAttribute("overviewSMP", overViewPengajarSMP);

        //SMA
        model.addAttribute("dataLabelSMA", dataLabelSMA);
        model.addAttribute("dataStatsSMA", durasiKonsultasiSMA);
        model.addAttribute("overviewSMA", overViewPengajarSMA);

        model.addAttribute("listLabelPengajar", listLabelPengajar);
        model.addAttribute("tahun-ajar", tahunAjar);
        model.addAttribute("month", month);
        model.addAttribute("listTahunAjar", listTahunAjar);

        return "statistik/statistics-konsultasi-pengajar";
    }
}
