package com.projectpop.quanta.statistics;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.service.KelasService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MapelService;
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

    @Qualifier("statisticsServiceImpl")
    @Autowired
    private StatisticsService statisticsService;


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
        Map<String, String> mapMapel = statisticsService.getKategoriTer(listNamaMapel, listPersentasePresensiMapel, tahunAjar,  month);

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
        Map<String, String> mapKelas = statisticsService.getKategoriTer(listNamaKelas, listPersentasePresensiKelas, tahunAjar,  month);

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

}
