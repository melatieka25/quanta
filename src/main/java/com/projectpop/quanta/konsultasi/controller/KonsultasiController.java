package com.projectpop.quanta.konsultasi.controller;

import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.service.KonsultasiService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MataPelajaranService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pengajarmapel.service.PengajarMapelService;
import com.projectpop.quanta.siswa.model.Jenjang;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.siswajadwalkelas.service.SiswaJadwalService;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.siswakonsultasi.service.SiswaKonsultasiService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.projectpop.quanta.konsultasi.model.StatusKonsul.*;


import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
//@RequestMapping("/konsultasi")
public class KonsultasiController {
    @Qualifier("konsultasiServiceImpl")
    @Autowired
    private KonsultasiService konsultasiService;

    @Qualifier("siswaKonsultasiServiceImpl")
    @Autowired
    private SiswaKonsultasiService siswaKonsultasiService;

    @Qualifier("siswaServiceImpl")
    @Autowired
    private SiswaService siswaService;

    @Qualifier("pengajarServiceImpl")
    @Autowired
    private PengajarService pengajarService;

    @Qualifier("mataPelajaranServiceImpl")
    @Autowired
    private MataPelajaranService mataPelajaranService;

    @Qualifier("pengajarMapelServiceImpl")
    @Autowired
    private PengajarMapelService pengajarMapelService;

    @Qualifier("siswaJadwalServiceImpl")
    @Autowired
    private SiswaJadwalService siswaJadwalService;

    @Qualifier("jadwalKelasServiceImpl")
    @Autowired
    private JadwalKelasService jadwalKelasService;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;

    @GetMapping("/konsultasi")
    public String viewJadwalKonsultasi(Model model, Principal principal) {
        UserModel user = userService.getUserByEmail(principal.getName());

        if(user.getRole().toString().equals("ADMIN")){
            List<KonsultasiModel> listKonsultasi = konsultasiService.getListKonsultasi();
            model.addAttribute("listKonsultasi", listKonsultasi);
            return "konsultasi/admin-viewall";
        }

        else if(user.getRole().toString().equals("PENGAJAR")){
            PengajarModel pengajar = pengajarService.findPengajarByEmail(principal.getName());
            List<KonsultasiModel> myListKonsultasi = konsultasiService.getListMyKonsultasiPengajar(pengajar);
            List<KonsultasiModel> myListRequestKonsultasi = konsultasiService.getListMyRequestKonsultasi(pengajar);

            model.addAttribute("myListKonsultasi", myListKonsultasi);
            model.addAttribute("myListRequestKonsultasi", myListRequestKonsultasi);
            return "konsultasi/landing-page-pengajar";

        }

        else if(user.getRole().toString().equals("SISWA")){
            SiswaModel siswa = siswaService.findSiswaByEmail(principal.getName());
            Jenjang jenjang = siswa.getJenjang();
            List<SiswaKonsultasiModel> listMySiswaKonsultasi = siswaKonsultasiService.getListKonsultasiBySiswa(siswa);
            List<KonsultasiModel> listKonsultasiJenjang = konsultasiService.getListKonsultasiByJenjang(jenjang);

            model.addAttribute("siswa", siswa);
            model.addAttribute("listMySiswaKonsultasi", listMySiswaKonsultasi);
            model.addAttribute("listKonsultasiJenjang", listKonsultasiJenjang);
            return "konsultasi/landing-page-siswa";
        }
        return "error";
    }

    @GetMapping("/konsultasi/view/{idKonsultasi}" )
    public String viewDetailKonsultasiPage(@PathVariable Integer idKonsultasi, Model model) {
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);
        List<SiswaKonsultasiModel> listSiswaKonsultasi = siswaKonsultasiService.getListSiswaByKonsultasi(konsultasi);

        for (SiswaKonsultasiModel siswaKonsultasi: listSiswaKonsultasi) {
            KelasModel kelas = siswaService.getKelasBimbel(siswaKonsultasi.getSiswaKonsul());
            siswaKonsultasi.getSiswaKonsul().setKelasBimbel(kelas);
        }

        model.addAttribute("konsultasi", konsultasi);
        model.addAttribute("listSiswaKonsultasi", listSiswaKonsultasi);

        return "konsultasi/view-detail";
    }

    @GetMapping("/konsultasi/siswa/view/{idKonsultasi}" )
    public String viewDetailKonsultasiPageSiswa(@PathVariable Integer idKonsultasi, Authentication authentication, Model model) {
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);
        SiswaModel siswa = siswaService.findSiswaByEmail(authentication.getName());
        List<SiswaKonsultasiModel> listSiswaKonsultasi = siswaKonsultasiService.getListSiswaByKonsultasi(konsultasi);

        for (SiswaKonsultasiModel siswaKonsultasi: listSiswaKonsultasi) {
            KelasModel kelas = siswaService.getKelasBimbel(siswaKonsultasi.getSiswaKonsul());
            siswaKonsultasi.getSiswaKonsul().setKelasBimbel(kelas);
        }

        model.addAttribute("konsultasi", konsultasi);
        model.addAttribute("siswa", siswa);
        model.addAttribute("listSiswaKonsultasi", listSiswaKonsultasi);

        return "konsultasi/siswa-view-detail";
    }


    @GetMapping("/konsultasi/cancel/{idKonsultasi}")
    public String cancelKonsultasiPage(Authentication authentication,
                                       @PathVariable Integer idKonsultasi,
                                       Model model,
                                       RedirectAttributes redirectAttrs) {
        SiswaModel siswa = siswaService.findSiswaByEmail(authentication.getName());
        SiswaKonsultasiModel siswaKonsultasi = siswaKonsultasiService.getBySiswaAndKonsultasi(siswa, idKonsultasi);
        KonsultasiModel konsultasiCancel = siswaKonsultasi.getKonsultasi();
        if (null != siswaKonsultasiService.cancelConsultation(siswaKonsultasi)) {
            model.addAttribute("konsultasiCancel", konsultasiCancel);

            List<SiswaKonsultasiModel> allListSiswaKonsultasi = siswaKonsultasiService.getListKonsultasiBySiswa(siswa);
            model.addAttribute("siswa", siswa);
            model.addAttribute("listSiswaKonsultasi", allListSiswaKonsultasi);

            redirectAttrs.addFlashAttribute("message", "Konsultasi berhasil dibatalkan!");

            return "redirect:/konsultasi";

        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Konsultasi gagal dibatalkan!");


            return "redirect:/konsultasi";
        }

    }


//    create konsultasi
    @GetMapping("/konsultasi/add")
    public String addKonsultasiFormPage(Model model) {
        KonsultasiModel konsultasi = new KonsultasiModel();
        getAllDropdownList(konsultasi, model);
        return "konsultasi/form-add";
    }

    private List<LocalTime> getListWaktuAwalKonsultasi(){
        List<LocalTime> listWaktuMulaiKonsul = new ArrayList<>();
        for (int i = 10; i < 20; i++) {
            listWaktuMulaiKonsul.add(LocalTime.of(i,00));
        }
        return listWaktuMulaiKonsul;
    }

    @PostMapping(value = "/konsultasi/add", params = {"save"})
    public String addKonsultasiSubmit(@ModelAttribute KonsultasiModel konsultasi,
                                      Authentication authentication,
                                      String waktuMulai,
                                      String tanggal,
                                      String topik,
                                      String mataPelajaran,
                                      String pengajarMapel,
                                      Model model,
                                      RedirectAttributes redirectAttrs) {


        SiswaModel siswa = siswaService.findSiswaByEmail(authentication.getName());

        konsultasi.setMapelKonsul(mataPelajaranService.getMapelById(Integer.parseInt(mataPelajaran)));
        konsultasi.setPengajarKonsul(pengajarService.getPengajarById(Integer.parseInt(pengajarMapel)));
        konsultasi.setTopic(topik);
        konsultasi.setDuration(1);
        LocalDateTime startTime = LocalDateTime.of(LocalDate.parse(tanggal), LocalTime.parse(waktuMulai));
        LocalDateTime endTime = LocalDateTime.of(LocalDate.parse(tanggal), LocalTime.parse(waktuMulai).plusHours(1));

        konsultasi.setStartTime(startTime);
        konsultasi.setEndTime(endTime);


        konsultasi.setStatus(PENDING);
        konsultasi.setJenjangKelas(siswa.getJenjang());

        KelasModel kelas = siswaService.getKelasBimbel(siswa);
        if (null == kelas){
            redirectAttrs.addFlashAttribute("errorMessage", "Anda belum memiliki kelas! Hubungi admin untuk mendaftarkan kelas");
            return "redirect:/";
        }
        siswa.setKelasBimbel(kelas);
        konsultasi.setTahunAjarKonsul(kelas.getTahunAjar());


        SiswaKonsultasiModel siswaKonsultasi = new SiswaKonsultasiModel();
        siswaKonsultasi.setSiswaKonsul(siswa);
        siswaKonsultasi.setKonsultasi(konsultasi);


        List<SiswaKonsultasiModel> listSiswaKonsultasi = new ArrayList<SiswaKonsultasiModel>();
        listSiswaKonsultasi.add(siswaKonsultasi);

        konsultasi.setListSiswaKonsultasi(listSiswaKonsultasi);

        if (siswa.getListKonsultasiSiswa() == null){
            siswa.setListKonsultasiSiswa(new ArrayList<SiswaKonsultasiModel>());
        }

        siswa.getListKonsultasiSiswa().add(siswaKonsultasi);

        konsultasiService.createKonsultasi(konsultasi);
        siswaService.updateSiswa(siswa);

        List<SiswaKonsultasiModel> allListSiswaKonsultasi = siswaKonsultasiService.getListKonsultasiBySiswa(siswa);
        model.addAttribute("siswa", siswa);
        model.addAttribute("listSiswaKonsultasi", allListSiswaKonsultasi);



        redirectAttrs.addFlashAttribute("message", "Konsultasi berhasil ditambahkan!");
        return "redirect:/konsultasi";
    }




    public void getAllDropdownList(KonsultasiModel konsultasi, Model model) {
        List<PengajarModel> listPengajar = pengajarService.getListPengajarActive();
        List<MataPelajaranModel> listMapel = mataPelajaranService.getListMapel();
        List<LocalTime> listWaktuMulaiKonsul = getListWaktuAwalKonsultasi();

        // pass data
        model.addAttribute("konsultasi", konsultasi);
        model.addAttribute("listMapel", listMapel);
        model.addAttribute("listPengajar", listPengajar);
        model.addAttribute("listWaktuMulaiKonsul", listWaktuMulaiKonsul);
    }

//    end create konsultasi



}

