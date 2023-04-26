package com.projectpop.quanta.jadwalkelas.controller;

import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.service.KelasService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MapelService;
import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import com.projectpop.quanta.pengajarmapel.service.PengajarMapelService;
import com.projectpop.quanta.user.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.model.UserRole;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;


import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;

@Controller
@RequestMapping("/jadwal-kelas")
public class JadwalKelasController {
    @Autowired
    private JadwalKelasService jadwalKelasService;

    @Autowired
    private MapelService mapelService;

    @Autowired
    private KelasService kelasService;

    @Autowired
    private PengajarService pengajarService;

    @Autowired
    private PengajarMapelService pengajarMapelService;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;

    @Autowired
    private OrtuService ortuService;

    @Autowired
    private SiswaService siswaService;

    // VIEW ALL
    @GetMapping("")
    public String viewAllJadwalKelas(Principal principal, Model model) {
        UserModel user = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(user,model);

        // ORTU
        if (user.getRole() == UserRole.ORTU) {
            OrtuModel ortu = ortuService.getOrtuById(user.getId());
           
            SiswaModel anak = ortuService.getDefaultAnakTerpilih(ortu);
            model.addAttribute("anak", anak);
            return "redirect:/jadwal-kelas/anak/" + anak.getId();

        }

        // ADMIN
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelas();
        model.addAttribute("listJadwal", listJadwalKelas);
        return "jadwalkelas/jadwalkelas-viewall";
    }

    @GetMapping("/anak/{id}")
    public String viewAllJadwalKelasOrtu(@PathVariable("id") Integer id, Principal principal, Model model) {
        SiswaModel siswa = siswaService.getSiswaById(id);
        List<JadwalKelasModel> listJadwalAnak = jadwalKelasService.getListJadwalKelasByKelas(siswaService.getKelasBimbel(siswa));
        // System.out.println("===== cek nama " + siswa.getName());
        model.addAttribute("listJadwal", listJadwalAnak);
        model.addAttribute("anak", siswa);

        return "jadwalkelas/jadwalkelas-viewall";
    }

    // VIEW ALL
    @GetMapping("/{id}")
    public String viewDetailJadwalKelas(@PathVariable("id") Integer id, Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        JadwalKelasModel jadwal = jadwalKelasService.getJadwalKelasById(id);
        LocalDateTime now = LocalDateTime.now();
        boolean canUpdate = true;
        if (now.isAfter(jadwal.getStartDateClass())) {
            canUpdate = false;
        } else if (now.isEqual(jadwal.getStartDateClass())) {
            canUpdate = false;
        } 
        model.addAttribute("jadwal", jadwal);
        model.addAttribute("canUpdate", canUpdate);
        return "jadwalkelas/jadwalkelas-view-detail";
    }

    // CREATE FORM
    @GetMapping("/add")
    public String addJadwalKelasFormPage(Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        JadwalKelasModel jadwalKelas = new JadwalKelasModel();
        getAllDropdownList(jadwalKelas, model);
        return "jadwalkelas/jadwalkelas-add-form";
    }

    // SUBMIT CREATE
    @PostMapping(value = "/add", params = {"save"})
    public String submitJadwalKelas(@ModelAttribute JadwalKelasModel jadwalKelas, String tanggal, String jam_mulai, String jam_selesai, 
    String kelasDiajar, String mapel, String pengajar,Model model, RedirectAttributes redirectAttrs) {
        // set atttribute
        jadwalKelas.setKelas(kelasService.getKelasById(Integer.parseInt(kelasDiajar)));
        jadwalKelas.setMapelJadwal(mapelService.getMapelById(Integer.parseInt(mapel)));
        jadwalKelas.setPengajarKelas(pengajarService.getPengajarById(Integer.parseInt(pengajar)));

        // parsing waktu mulai dan selesai
        LocalDateTime waktuMulai = LocalDateTime.of(LocalDate.parse(tanggal), LocalTime.parse(jam_mulai));
        LocalDateTime waktuSelesai = LocalDateTime.of(LocalDate.parse(tanggal), LocalTime.parse(jam_selesai));
        jadwalKelas.setStartDateClass(waktuMulai);
        jadwalKelas.setEndDateClass(waktuSelesai);

        List<Object> result = cekBentrok(jadwalKelas, model);
        boolean isBentrok = (Boolean) result.get(0);
        if (!isBentrok) {
            jadwalKelasService.addJadwalKelas(jadwalKelas);
            redirectAttrs.addFlashAttribute("message", String.valueOf(result.get(1)));
            return "redirect:/jadwal-kelas/" + jadwalKelas.getId();
        }

        redirectAttrs.addFlashAttribute("message", String.valueOf(result.get(1)));
        getAllDropdownList(jadwalKelas, model);

        return "redirect:/jadwal-kelas/add";
    }

    public void createUpdateList(JadwalKelasModel jadwalKelas, Model model) {
        LocalDateTime jadwalMulai = jadwalKelas.getStartDateClass();
        LocalDateTime jadwalSelesai = jadwalKelas.getEndDateClass();
        LocalDate date = LocalDate.of(jadwalMulai.getYear(), jadwalMulai.getMonth(), jadwalMulai.getDayOfMonth());
        LocalTime jamMulai = LocalTime.of(jadwalMulai.getHour(), jadwalMulai.getMinute(), jadwalMulai.getSecond());
        LocalTime jamSelesai = LocalTime.of(jadwalSelesai.getHour(), jadwalSelesai.getMinute(), jadwalSelesai.getSecond());

        int day = jadwalKelas.getStartDateClass().getDayOfWeek().getValue();
        model.addAttribute("listKelas", kelasService.getListKelasByDays(day));

        PengajarModel pengajar = jadwalKelas.getPengajarKelas();
        model.addAttribute("listMapel", pengajarMapelService.getListMapelByPengajar(pengajar));

        MataPelajaranModel mapel = jadwalKelas.getMapelJadwal();
        List<PengajarModel> listPengajarActive = new ArrayList<>();

        for (PengajarMapelModel peMapel : pengajarMapelService.getListPengajarByMapel(mapel)) {
            if (peMapel.getPengajar().getIsActive()) {
                listPengajarActive.add(peMapel.getPengajar());
            }
        }
        model.addAttribute("listPengajar", listPengajarActive);

        model.addAttribute("tanggal", date);
        model.addAttribute("jamMulai", jamMulai);
        model.addAttribute("jamSelesai", jamSelesai);
        model.addAttribute("jadwalKelas", jadwalKelas); 
    }

    // FORM UPDATE
    @GetMapping("/update/{id}")
    public String updateJadwalKelasFormPage(@PathVariable("id") Integer id, Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        JadwalKelasModel jadwalKelas = jadwalKelasService.getJadwalKelasById(id);
        getAllDropdownList(jadwalKelas, model);

        LocalDateTime jadwalMulai = jadwalKelas.getStartDateClass();
        LocalDateTime jadwalSelesai = jadwalKelas.getEndDateClass();
        LocalDate date = LocalDate.of(jadwalMulai.getYear(), jadwalMulai.getMonth(), jadwalMulai.getDayOfMonth());
        LocalTime jamMulai = LocalTime.of(jadwalMulai.getHour(), jadwalMulai.getMinute(), jadwalMulai.getSecond());
        LocalTime jamSelesai = LocalTime.of(jadwalSelesai.getHour(), jadwalSelesai.getMinute(), jadwalSelesai.getSecond());

        int day = jadwalKelas.getStartDateClass().getDayOfWeek().getValue();
        model.addAttribute("listKelas", kelasService.getListKelasByDays(day));

        // PengajarModel pengajar = jadwalKelas.getPengajarKelas();
        // model.addAttribute("listMapel", pengajarMapelService.getListMapelByPengajar(pengajar));

        MataPelajaranModel mapel = jadwalKelas.getMapelJadwal();
        model.addAttribute("listPengajar", pengajarMapelService.getListPengajarByMapel(mapel));

        model.addAttribute("tanggal", date);
        model.addAttribute("jamMulai", jamMulai);
        model.addAttribute("jamSelesai", jamSelesai);
        model.addAttribute("jadwalKelas", jadwalKelas);  

        // reset list presensi and delete presensimodel
        jadwalKelasService.updateJadwalKelas(jadwalKelas);

        return "jadwalkelas/jadwalkelas-update-form";
    }

    // SUBMIT FORM
    @PostMapping(value="/update", params={"update"})
    public String updateJadwalKelasSubmit(@ModelAttribute JadwalKelasModel jadwalKelas, String tanggal, String jam_mulai, String jam_selesai,
    String kelasDiajar, String mapel, String pengajar, Model model, RedirectAttributes redirectAttrs) {
         // set atttribute
         jadwalKelas.setKelas(kelasService.getKelasById(Integer.parseInt(kelasDiajar)));
         jadwalKelas.setMapelJadwal(mapelService.getMapelById(Integer.parseInt(mapel)));
         jadwalKelas.setPengajarKelas(pengajarService.getPengajarById(Integer.parseInt(pengajar)));
 
         // parsing waktu mulai dan selesai
         LocalDateTime waktuMulai = LocalDateTime.of(LocalDate.parse(tanggal), LocalTime.parse(jam_mulai));
         LocalDateTime waktuSelesai = LocalDateTime.of(LocalDate.parse(tanggal), LocalTime.parse(jam_selesai));
         jadwalKelas.setStartDateClass(waktuMulai);
         jadwalKelas.setEndDateClass(waktuSelesai);
 
        // cek waktu update
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(jadwalKelas.getStartDateClass())) {
            redirectAttrs.addFlashAttribute("message", "Jadwal sedang atau sudah berlangsung! Perubahan jadwal gagal dilakukan");
            getAllDropdownList(jadwalKelas, model);
            createUpdateList(jadwalKelas, model);
            return "redirect:/jadwal-kelas/update/" + jadwalKelas.getId();
        }

        // cek waktu bentrok
        List<Object> result = cekBentrok(jadwalKelas, model);
        boolean isBentrok = (Boolean) result.get(0);
        if (isBentrok) {
            getAllDropdownList(jadwalKelas, model);
            createUpdateList(jadwalKelas, model);
            redirectAttrs.addFlashAttribute("message", String.valueOf(result.get(1)));
            return "redirect:/jadwal-kelas/update/" + jadwalKelas.getId();
        }

        jadwalKelasService.addJadwalKelas(jadwalKelas);
        redirectAttrs.addFlashAttribute("message", "Perubahan jadwal berhasil dilakukan!");
        return "redirect:/jadwal-kelas/" + jadwalKelas.getId();
    }

    public List<Object> cekBentrok(JadwalKelasModel jadwalKelas, Model model) {
        LocalDateTime waktuMulai = jadwalKelas.getStartDateClass();
        LocalDateTime waktuSelesai = jadwalKelas.getEndDateClass();
        // pengecekan bentrok
        boolean isBentrok = false;
        String message = "Jadwal bentrok! Penyimpanan jadwal gagal dilakukan.";
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelas();
        for (JadwalKelasModel jadwalFromDb : listJadwalKelas) {

            if (jadwalFromDb.getId() != jadwalKelas.getId()){    
                //cek kesamaan yang mungkin menyebabkan bentrok
                // System.out.println(jadwalFromDb.getId() + "======" +jadwalKelas.getId());

                // cek kesamaan pengajar
                boolean isPossible = false;
                if (jadwalKelas.getPengajarKelas().getId().equals(jadwalFromDb.getPengajarKelas().getId())) {
                    message = "Jadwal bentrok! Pengajar sudah mengajar kelas lain di hari tersebut. Penyimpanan jadwal gagal dilakukan.";
                    isPossible = true;
                // cek kesamaan kelas
                } else if (jadwalKelas.getKelas().getId().equals(jadwalFromDb.getKelas().getId())){
                    message = "Jadwal bentrok! Kelas sudah memiliki jadwal belajar di hari tersebut. Penyimpanan jadwal gagal dilakukan.";
                    isPossible = true;
                // cek kesamaan ruang kelas
                } else if (jadwalKelas.getRuangKelas().toLowerCase().equals(jadwalFromDb.getRuangKelas().toLowerCase())) {
                    message = "Jadwal bentrok! Ruang kelas sudah dipakai di hari tersebut. Penyimpanan jadwal gagal dilakukan.";
                    isPossible = true;
                }

                if (isPossible) {
                    LocalDateTime startDateFromDB = jadwalFromDb.getStartDateClass();
                    LocalDateTime endDateFromDB = jadwalFromDb.getEndDateClass();

                    // cek bentrok jadwal
                    if (waktuMulai.isEqual(startDateFromDB) || waktuSelesai.isEqual(endDateFromDB)) {
                        isBentrok = true;
                        break;
                    }else if (waktuMulai.isAfter(startDateFromDB) && waktuMulai.isBefore(endDateFromDB)) {
                        isBentrok = true;
                        break;
                    }else if (waktuSelesai.isAfter(startDateFromDB) && waktuSelesai.isBefore(endDateFromDB)) {
                        isBentrok = true;
                        break;
                    }else if (waktuMulai.isAfter(startDateFromDB) && waktuSelesai.isBefore(endDateFromDB)) {
                        isBentrok = true;
                        break;
                    }else if (waktuMulai.isBefore(startDateFromDB) && waktuSelesai.isAfter(endDateFromDB)) {
                        isBentrok = true;
                        break;
                    }
                }
            }
        }

        List<Object> result = new ArrayList<>();

        // error message
        if (isBentrok) {
            result.add(isBentrok);
            result.add(message);
        } else {
            result.add(isBentrok);
            result.add("Jadwal kelas berhasil disimpan!");
        }

        return result;
    }

    public void getAllDropdownList(JadwalKelasModel jadwalKelas, Model model) {
           // list dropdown
           List<KelasModel> listKelas = kelasService.getListKelas();
           List<PengajarModel> listPengajar = pengajarService.getListPengajarActive();
           List<MataPelajaranModel> listMapel = mapelService.getAllMapel();
           List<String> listRuangKelas = new ArrayList<>();
           listRuangKelas.add("ferarri");
           listRuangKelas.add("Subaru");
           listRuangKelas.add("Lambo");
           listRuangKelas.add("toyota");
           listRuangKelas.add("mazda");
           listRuangKelas.add("kijang");
           
           // pass data
           model.addAttribute("jadwalKelas", jadwalKelas);
           model.addAttribute("listMapel", listMapel);
           model.addAttribute("listPengajar", listPengajar);
           model.addAttribute("listRuangKelas", listRuangKelas);
           model.addAttribute("listKelas", listKelas);
    }


    @GetMapping("/delete/{id}")
    public String deleteJadwalKelas(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttrs, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        JadwalKelasModel jadwalKelas = jadwalKelasService.getJadwalKelasById(id);
        LocalDateTime now = LocalDateTime.now();
        boolean isOngoing = false;
        boolean hasPassed = false;
        if (now.isAfter(jadwalKelas.getStartDateClass()) && now.isBefore(jadwalKelas.getEndDateClass())) {
            isOngoing = true;
        } else if (now.isEqual(jadwalKelas.getStartDateClass())) {
            isOngoing = true;
        } else if (now.isAfter(jadwalKelas.getEndDateClass())) {
            hasPassed = true;
        }

        if (isOngoing) {
            redirectAttrs.addFlashAttribute("deletefailed", "Jadwal sedang berlangsung! Penghapusan jadwal gagal dilakukan");
            return "redirect:/jadwal-kelas/" + id;
        } else if (hasPassed){
            redirectAttrs.addFlashAttribute("deletefailed", "Jadwal sudah selesai berlangsung! Penghapusan jadwal gagal dilakukan");
            return "redirect:/jadwal-kelas/" + id;
        }else {
            redirectAttrs.addFlashAttribute("message", "Jadwal berhasil dihapus!");
            jadwalKelasService.deleteJadwalKelas(jadwalKelas);
        }

        // List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelas();
        // model.addAttribute("listJadwal", listJadwalKelas);
        return "redirect:/jadwal-kelas/";
    }



}
