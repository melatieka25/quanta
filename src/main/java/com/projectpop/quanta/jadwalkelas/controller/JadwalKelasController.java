package com.projectpop.quanta.jadwalkelas.controller;

import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.service.KelasService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MataPelajaranService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private MataPelajaranService mataPelajaranService;

    @Autowired
    private KelasService kelasService;

    @Autowired
    private PengajarService pengajarService;

    // VIEW ALL
    @GetMapping("")
    public String viewAllJadwalKelas(Model model) {
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelas();
        model.addAttribute("listJadwal", listJadwalKelas);
        return "jadwalkelas/jadwalkelas-viewall";
    }

    // CREATE FORM
    @GetMapping("/add")
    public String addJadwalKelasFormPage(Model model) {
        JadwalKelasModel jadwalKelas = new JadwalKelasModel();
        getAllDropdownList(jadwalKelas, model);
        return "jadwalkelas/jadwalkelas-add-form";
    }

    // SUBMIT CREATE
    @PostMapping(value = "/add", params = {"save"})
    public String submitJadwalKelas(@ModelAttribute JadwalKelasModel jadwalKelas, String tanggal, String jam_mulai, String jam_selesai, 
    String kelasDiajar, String mapel, String pengajar,Model model) {
        // set atttribute
        jadwalKelas.setKelas(kelasService.getKelasById(Integer.parseInt(kelasDiajar)));
        jadwalKelas.setMapelJadwal(mataPelajaranService.getMapelById(Integer.parseInt(mapel)));
        jadwalKelas.setPengajarKelas(pengajarService.getPengajarById(Integer.parseInt(pengajar)));

        // parsing waktu mulai dan selesai
        LocalDateTime waktuMulai = LocalDateTime.of(LocalDate.parse(tanggal), LocalTime.parse(jam_mulai));
        LocalDateTime waktuSelesai = LocalDateTime.of(LocalDate.parse(tanggal), LocalTime.parse(jam_selesai));
        jadwalKelas.setStartDateClass(waktuMulai);
        jadwalKelas.setEndDateClass(waktuSelesai);
        
        // // pengecekan bentrok
        // boolean isBentrok = false;
        // String message = "Jadwal bentrok! Penyimpanan jadwal gagal dilakukan.";
        // List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelas();
        // for (JadwalKelasModel jadwalFromDb : listJadwalKelas) {

        //     //cek kesamaan yang mungkin menyebabkan bentrok

        //     // cek kesamaan pengajar
        //     boolean isPossible = false;
        //     if (jadwalKelas.getPengajarKelas().getId().equals(jadwalFromDb.getPengajarKelas().getId())) {
        //         message = "Jadwal bentrok! Pengajar sudah mengajar kelas lain di hari tersebut. Penyimpanan jadwal gagal dilakukan.";
        //         isPossible = true;
        //     // cek kesamaan kelas
        //     } else if (jadwalKelas.getKelas().getId().equals(jadwalFromDb.getKelas().getId())){
        //         message = "Jadwal bentrok! Kelas sudah memiliki jadwal belajar di hari tersebut. Penyimpanan jadwal gagal dilakukan.";
        //         isPossible = true;
        //     // cek kesamaan ruang kelas
        //     } else if (jadwalKelas.getRuangKelas().toLowerCase().equals(jadwalFromDb.getRuangKelas().toLowerCase())) {
        //         message = "Jadwal bentrok! Ruang kelas sudah dipakai di hari tersebut. Penyimpanan jadwal gagal dilakukan.";
        //         isPossible = true;
        //     }

        //     if (isPossible) {
        //         LocalDateTime startDateFromDB = jadwalFromDb.getStartDateClass();
        //         LocalDateTime endDateFromDB = jadwalFromDb.getEndDateClass();

        //         // cek bentrok jadwal
        //         if (waktuMulai.isEqual(startDateFromDB) || waktuSelesai.isEqual(endDateFromDB)) {
        //             isBentrok = true;
        //             break;
        //         }else if (waktuMulai.isAfter(startDateFromDB) && waktuMulai.isBefore(endDateFromDB)) {
        //             isBentrok = true;
        //             break;
        //         }else if (waktuSelesai.isAfter(startDateFromDB) && waktuSelesai.isBefore(endDateFromDB)) {
        //             isBentrok = true;
        //             break;
        //         }else if (waktuMulai.isBefore(startDateFromDB) && waktuSelesai.isAfter(endDateFromDB)) {
        //             isBentrok = true;
        //             break;
        //         }
        //     }
        // }

        // // error message
        // if (isBentrok) {
        //     System.out.println("bentrok");
        //     model.addAttribute("message", message);
        // } else {
        //     System.out.println("tidak bentrok");
        //     jadwalKelasService.addJadwalKelas(jadwalKelas);
        //     model.addAttribute("message", "Jadwal kelas berhasil disimpan!");
        //     model.addAttribute("listJadwal", jadwalKelasService.getListJadwalKelas());
        //     return "jadwalkelas/jadwalkelas-viewall";
        // }

        if (!cekBentrok(jadwalKelas, model)) {
            jadwalKelasService.addJadwalKelas(jadwalKelas);
            model.addAttribute("listJadwal", jadwalKelasService.getListJadwalKelas());
            return "jadwalkelas/jadwalkelas-viewall";
        }

        getAllDropdownList(jadwalKelas, model);

        return "jadwalkelas/jadwalkelas-add-form";
    }

    // FORM UPDATE
    @GetMapping("/update/{id}")
    public String updateJadwalKelasFormPage(@PathVariable("id") Integer id, Model model) {
        JadwalKelasModel jadwalKelas = jadwalKelasService.getJadwalKelasById(id);
        getAllDropdownList(jadwalKelas, model);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        // Date date = formatter.parse(formatter.format(jadwalKelas.getStartDateClass()));

        formatter = new SimpleDateFormat("hh:mm");
        // LocalTime time = date.getTime();

        model.addAttribute("jadwalKelas", jadwalKelas);
        // model.addAttribute("tanggal", jadwalKelas.getStar)
        
        return "jadwalkelas/jadwalkelas-update-form";
    }

    // SUBMIT FORM
    @PostMapping("/update")
    public String updateJadwalKelasSubmit(@ModelAttribute JadwalKelasModel jadwalKelas, Model model) {
        // cek waktu update
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(jadwalKelas.getStartDateClass())) {
            model.addAttribute("message", "Jadwal sedang atau sudah berlangsung! Perubahan jadwal gagal dilakukan");
            getAllDropdownList(jadwalKelas, model);
            return "jadwalkelas/jadwalkelas-update-form";

        }

        // cek waktu bentrok
        if (cekBentrok(jadwalKelas, model)) {
            jadwalKelasService.updateJadwalKelas(jadwalKelas);
            getAllDropdownList(jadwalKelas, model);
            return "jadwalkelas/jadwalkelas-update-form";
        }

        jadwalKelasService.updateJadwalKelas(jadwalKelas);
        model.addAttribute("listJadwal", jadwalKelasService.getListJadwalKelas());
        return "jadwalkelas/jadwalkelas-viewall";
    }

    public boolean cekBentrok(JadwalKelasModel jadwalKelas, Model model) {
        LocalDateTime waktuMulai = jadwalKelas.getStartDateClass();
        LocalDateTime waktuSelesai = jadwalKelas.getEndDateClass();
        // pengecekan bentrok
        boolean isBentrok = false;
        String message = "Jadwal bentrok! Penyimpanan jadwal gagal dilakukan.";
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelas();
        for (JadwalKelasModel jadwalFromDb : listJadwalKelas) {

            //cek kesamaan yang mungkin menyebabkan bentrok

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
                }else if (waktuMulai.isBefore(startDateFromDB) && waktuSelesai.isAfter(endDateFromDB)) {
                    isBentrok = true;
                    break;
                }
            }
        }

        // error message
        if (isBentrok) {
            System.out.println("bentrok");
            model.addAttribute("message", message);
        } else {
            System.out.println("tidak bentrok");
            // jadwalKelasService.addJadwalKelas(jadwalKelas);
            model.addAttribute("message", "Jadwal kelas berhasil disimpan!");
            // model.addAttribute("listJadwal", jadwalKelasService.getListJadwalKelas());
            // return "jadwalkelas/jadwalkelas-viewall";
        }

        return isBentrok;
    }

    public void getAllDropdownList(JadwalKelasModel jadwalKelas, Model model) {
           // list dropdown
           List<KelasModel> listKelas = kelasService.getListKelas();
           List<PengajarModel> listPengajar = pengajarService.getListPengajar();
           List<MataPelajaranModel> listMapel = mataPelajaranService.getListMapel();
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
    public String deleteJadwalKelas(@PathVariable("id") Integer id, Model model) {
        JadwalKelasModel jadwalKelas = jadwalKelasService.getJadwalKelasById(id);
        LocalDateTime now = LocalDateTime.now();
        boolean isOngoing = false;
        if (now.isAfter(jadwalKelas.getStartDateClass()) && now.isBefore(jadwalKelas.getEndDateClass())) {
            isOngoing = true;
        } else if (now.isEqual(jadwalKelas.getStartDateClass())) {
            isOngoing = true;
        }

        if (isOngoing) {
            model.addAttribute("deletefailed", "Jadwal sedang berlangsung! Penghapusan jadwal gagal dilakukan");
        } else {
            model.addAttribute("message", "Jadwal berhasil dihapus!");
            jadwalKelasService.deleteJadwalKelas(jadwalKelas);
        }

        List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelas();
        model.addAttribute("listJadwal", listJadwalKelas);
        return "jadwalkelas/jadwalkelas-viewall";
    }



}
