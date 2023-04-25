package com.projectpop.quanta.konsultasi.controller;

import com.projectpop.quanta.email.service.EmailService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.service.KonsultasiService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MapelService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.siswa.model.Jenjang;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
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

    @Qualifier("mapelServiceImpl")
    @Autowired
    private MapelService mapelService;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/konsultasi")
    public String viewJadwalKonsultasi(Model model, Principal principal) {
        konsultasiService.reloadStatus();
        UserModel user = userService.getUserByEmail(principal.getName());

        if(user.getRole().toString().equals("ADMIN")){
            List<KonsultasiModel> listKonsultasiHariIni = konsultasiService.getListKonsultasiHariIni();
            List<KonsultasiModel> listKonsultasiPending = konsultasiService.getListKonsultasiStatus(PENDING);
            List<KonsultasiModel> listKonsultasiDiterima = konsultasiService.getListKonsultasiStatus(DITERIMA);
            List<KonsultasiModel> listKonsultasiDitolak = konsultasiService.getListKonsultasiStatus(DITOLAK);
            List<KonsultasiModel> listKonsultasiDitutup = konsultasiService.getListKonsultasiStatus(DITUTUP);
            List<KonsultasiModel> listKonsultasiKadaluarsa = konsultasiService.getListKonsultasiStatus(KADALUARSA);

            model.addAttribute("listKonsultasiHariIni", listKonsultasiHariIni);
            model.addAttribute("listKonsultasiPending", listKonsultasiPending);
            model.addAttribute("listKonsultasiDiterima", listKonsultasiDiterima);
            model.addAttribute("listKonsultasiDitolak", listKonsultasiDitolak);
            model.addAttribute("listKonsultasiDitutup", listKonsultasiDitutup);
            model.addAttribute("listKonsultasiKadaluarsa", listKonsultasiKadaluarsa);
            return "konsultasi/admin-viewall";
        }

        else if(user.getRole().toString().equals("PENGAJAR")){
            PengajarModel pengajar = pengajarService.getPengajarByEmail(principal.getName());
            List<KonsultasiModel> myListKonsultasiHariIni = konsultasiService.getListKonsultasiByPengajarAndTanggal(pengajar, LocalDate.now());
            List<KonsultasiModel> myListKonsultasiPending = konsultasiService.getListMyKonsultasiPengajarAndStatus(pengajar, PENDING);
            List<KonsultasiModel> myListKonsultasiDiterima = konsultasiService.getListMyKonsultasiPengajarAndStatus(pengajar, DITERIMA);
            List<KonsultasiModel> myListKonsultasiDitolak = konsultasiService.getListMyKonsultasiPengajarAndStatus(pengajar, DITOLAK);
            List<KonsultasiModel> myListKonsultasiDitutup = konsultasiService.getListMyKonsultasiPengajarAndStatus(pengajar, DITUTUP);
            List<KonsultasiModel> myListKonsultasiKadaluarsa = konsultasiService.getListMyKonsultasiPengajarAndStatus(pengajar, KADALUARSA);

            model.addAttribute("myListKonsultasiHariIni", myListKonsultasiHariIni);
            model.addAttribute("myListKonsultasiPending", myListKonsultasiPending);
            model.addAttribute("myListKonsultasiDiterima", myListKonsultasiDiterima);
            model.addAttribute("myListKonsultasiDitolak", myListKonsultasiDitolak);
            model.addAttribute("myListKonsultasiDitutup", myListKonsultasiDitutup);
            model.addAttribute("myListKonsultasiKadaluarsa", myListKonsultasiKadaluarsa);
            return "konsultasi/landing-page-pengajar";

        }

        else if(user.getRole().toString().equals("SISWA")){
            SiswaModel siswa = siswaService.findSiswaByEmail(principal.getName());
            Jenjang jenjang = siswa.getJenjang();
            List<SiswaKonsultasiModel> listMySiswaKonsultasiHariIni = siswaKonsultasiService.getListKonsultasiBySiswaAndTanggal(siswa, LocalDate.now());
            List<SiswaKonsultasiModel> listMySiswaKonsultasiPending = siswaKonsultasiService.getListKonsultasiBySiswaAndStatus(siswa, PENDING);
            List<SiswaKonsultasiModel> listMySiswaKonsultasiDiterima = siswaKonsultasiService.getListKonsultasiBySiswaAndStatus(siswa, DITERIMA);
            List<SiswaKonsultasiModel> listMySiswaKonsultasiDitolak = siswaKonsultasiService.getListKonsultasiBySiswaAndStatus(siswa, DITOLAK);
            List<SiswaKonsultasiModel> listMySiswaKonsultasiDitutup = siswaKonsultasiService.getListKonsultasiBySiswaAndStatus(siswa, DITUTUP);
            List<SiswaKonsultasiModel> listMySiswaKonsultasiKadaluarsa = siswaKonsultasiService.getListKonsultasiBySiswaAndStatus(siswa, KADALUARSA);


            List<KonsultasiModel> listKonsultasiJenjangHariIni = konsultasiService.getListKonsultasiByJenjangHariIni(jenjang);
            List<KonsultasiModel> listKonsultasiJenjangPending = konsultasiService.getListKonsultasiByJenjangAndStatus(jenjang, PENDING);
            List<KonsultasiModel> listKonsultasiJenjangDiterima = konsultasiService.getListKonsultasiByJenjangAndStatus(jenjang, DITERIMA);
            List<KonsultasiModel> listKonsultasiJenjangDitolak = konsultasiService.getListKonsultasiByJenjangAndStatus(jenjang, DITOLAK);
            List<KonsultasiModel> listKonsultasiJenjangDitutup = konsultasiService.getListKonsultasiByJenjangAndStatus(jenjang, DITUTUP);
            List<KonsultasiModel> listKonsultasiJenjangKadaluarsa = konsultasiService.getListKonsultasiByJenjangAndStatus(jenjang, KADALUARSA);

            model.addAttribute("siswa", siswa);
            model.addAttribute("listMySiswaKonsultasiHariIni", listMySiswaKonsultasiHariIni);
            model.addAttribute("listMySiswaKonsultasiPending", listMySiswaKonsultasiPending);
            model.addAttribute("listMySiswaKonsultasiDiterima", listMySiswaKonsultasiDiterima);
            model.addAttribute("listMySiswaKonsultasiDitolak", listMySiswaKonsultasiDitolak);
            model.addAttribute("listMySiswaKonsultasiDitutup", listMySiswaKonsultasiDitutup);
            model.addAttribute("listMySiswaKonsultasiKadaluarsa", listMySiswaKonsultasiKadaluarsa);

            model.addAttribute("listKonsultasiJenjangHariIni", listKonsultasiJenjangHariIni);
            model.addAttribute("listKonsultasiJenjangPending", listKonsultasiJenjangPending);
            model.addAttribute("listKonsultasiJenjangDiterima", listKonsultasiJenjangDiterima);
            model.addAttribute("listKonsultasiJenjangDitolak", listKonsultasiJenjangDitolak);
            model.addAttribute("listKonsultasiJenjangDitutup", listKonsultasiJenjangDitutup);
            model.addAttribute("listKonsultasiJenjangKadaluarsa", listKonsultasiJenjangKadaluarsa);
            return "konsultasi/landing-page-siswa";
        }
        return "error";
    }

    @GetMapping("/konsultasi/view/{idKonsultasi}" )
    public String viewDetailKonsultasiPage(@PathVariable Integer idKonsultasi, Principal principal, Model model) {
        UserModel user = userService.getUserByEmail(principal.getName());

        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);
        List<SiswaKonsultasiModel> listSiswaKonsultasi = siswaKonsultasiService.getListSiswaByKonsultasi(konsultasi);

        for (SiswaKonsultasiModel siswaKonsultasi: listSiswaKonsultasi) {
            KelasModel kelas = siswaService.getKelasBimbel(siswaKonsultasi.getSiswaKonsul());
            siswaKonsultasi.getSiswaKonsul().setKelasBimbel(kelas);
        }

        if(user.getRole().toString().equals("PENGAJAR")){
            boolean isToValidate = false;
            boolean isToClose = false;

            if (konsultasi.getStatus().equals(PENDING)){
                isToValidate = true;
            } else if (konsultasi.getStatus().equals(DITERIMA)
                    && konsultasi.getEndTime().isBefore(LocalDateTime.now())
                    && konsultasi.getStartTime().isBefore(LocalDateTime.now())) {
                isToClose = true;
            }

            model.addAttribute("isToValidate", isToValidate);
            model.addAttribute("isToClose", isToClose);
        }

        if(user.getRole().toString().equals("SISWA")){
            boolean isJoinable = false;
            boolean isCancelable = false;
            boolean isExtendable = false;

            if (konsultasi.getStatus().equals(PENDING)){
                if (null!=siswaKonsultasiService.getBySiswaAndKonsultasi((SiswaModel) user, konsultasi.getId())){
                    isCancelable = true;
                } else {
                    isJoinable = true;
                }
            }
            else if (konsultasi.getStatus().equals(DITERIMA)) {
                if (null!=siswaKonsultasiService.getBySiswaAndKonsultasi((SiswaModel) user, konsultasi.getId())){
                    if (konsultasi.getStartTime().isBefore(LocalDateTime.now())
                            && konsultasi.getEndTime().isAfter(LocalDateTime.now())){
                        isExtendable = true;
                    }
                }
                else {
                    if (konsultasi.getStartTime().minusMinutes(10).isAfter(LocalDateTime.now())){
                        isJoinable = true;
                    }
                }


            }


            model.addAttribute("isCancelable", isCancelable);
            model.addAttribute("isExtendable", isExtendable);
            model.addAttribute("isJoinable", isJoinable);
        }

        model.addAttribute("role", user.getRole().toString());
        model.addAttribute("konsultasi", konsultasi);
        model.addAttribute("listSiswaKonsultasi", listSiswaKonsultasi);

        return "konsultasi/view-detail";
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


    @GetMapping("/konsultasi/add")
    public String addKonsultasiFormPage(Model model) {
        KonsultasiModel konsultasi = new KonsultasiModel();
        List<MataPelajaranModel> listMapel = mapelService.getAllMapel();

        model.addAttribute("listMapel", listMapel);
        return "konsultasi/form-add";
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

        konsultasi.setMapelKonsul(mapelService.getMapelById(Integer.parseInt(mataPelajaran)));
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

        String formattedDate = getFormattedDate(konsultasi.getStartTime().toLocalDate());

        String emailPenerima = konsultasi.getPengajarKonsul().getEmail();
        String emailSubject = "ANDA MENDAPATKAN PERMINTAAN KONSULTASI BARU!";
        String emailBody = "Mohon segera konfirmasi sebelum  " + formattedDate + " pukul " + konsultasi.getStartTime().minusHours(2).toLocalTime()
                + "\n\nDetail konsultasi"
                + "\n- Tanggal: " + formattedDate
                + "\n- Waktu konsultasi: " + konsultasi.getStartTime().toLocalTime() + " - " + konsultasi.getEndTime().toLocalTime()
                + "\n- Jenjang: " + konsultasi.getJenjangKelas().getDisplayValue()
                + "\n- Mata Pelajaran: " + konsultasi.getMapelKonsul().getName()
                + "\n- Topik: " + konsultasi.getTopic();

        emailService.sendEmail(emailPenerima, emailSubject, emailBody);

        emailPenerima = authentication.getName();
        emailSubject = "PERMINTAAN KONSULTASI BERHASIL DIBUAT";
        emailBody = "Jika pengajar belum mengkonfimasi sebelum " + formattedDate + " pukul " + konsultasi.getStartTime().minusHours(2).toLocalTime()
                + ", maka konsultasi akan otomatis kadaluarsa"
                + "\n\nDetail konsultasi"
                + "\n- Tanggal: " + formattedDate
                + "\n- Waktu konsultasi: " + konsultasi.getStartTime().toLocalTime() + " - " + konsultasi.getEndTime().toLocalTime()
                + "\n- Mata Pelajaran: " + konsultasi.getMapelKonsul().getName()
                + "\n- Topik: " + konsultasi.getTopic();

        emailService.sendEmail(emailPenerima, emailSubject, emailBody);

        redirectAttrs.addFlashAttribute("message", "Konsultasi berhasil ditambahkan!");
        return "redirect:/konsultasi";
    }

    @PostMapping(value = "/konsultasi/terima/{idKonsultasi}", params = {"save"})
    public String submitFormTerimaKonsultasi(Principal principal,  @PathVariable Integer idKonsultasi, String place, RedirectAttributes redirectAttributes) {
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);
        PengajarModel pengajar = pengajarService.getPengajarByEmail(principal.getName());

        String formattedDate = getFormattedDate(konsultasi.getStartTime().toLocalDate());

        if (konsultasiService.getIsPengajarAvailable(pengajar, konsultasi)){
            konsultasi.setStatus(DITERIMA);
            konsultasi.setPlace(place);
            konsultasiService.updateKonsultasi(konsultasi);

            String emailSubject = "PENGAJAR TELAH MENERIMA KONSULTASI!";
            String emailBody = "Pembatalan konsultasi sudah tidak dapat dilakukan, mohon ikuti konsultasi sesuai jadwal!"
                    + "\n\nDetail konsultasi"
                    + "\n- Tanggal: " + formattedDate
                    + "\n- Waktu konsultasi: " + konsultasi.getStartTime().toLocalTime() + " - " + konsultasi.getEndTime().toLocalTime()
                    + "\n- Tempat: " + konsultasi.getPlace()
                    + "\n- Mata Pelajaran: " + konsultasi.getMapelKonsul().getName()
                    + "\n- Topik: " + konsultasi.getTopic();

            List<SiswaKonsultasiModel> listSiswaKonsul = siswaKonsultasiService.getListSiswaByKonsultasi(konsultasi);
            for (SiswaKonsultasiModel siwaKonsul: listSiswaKonsul) {
                emailService.sendEmail(siwaKonsul.getSiswaKonsul().getEmail(), emailSubject, emailBody);
            }

            redirectAttributes.addFlashAttribute("message", "Konsultasi berhasil diterima");
            return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page
        }

        String emailSubject = "PENGAJAR MENOLAK KONSULTASI!";
        String emailBody = "Pengajar telah menolak permintaan konsultasi yang kamu ikuti, mohon cari jadwal konsultasi lain."
                + "\n\nDetail konsultasi"
                + "\n- Tanggal: " + formattedDate
                + "\n- Waktu konsultasi: " + konsultasi.getStartTime().toLocalTime() + " - " + konsultasi.getEndTime().toLocalTime()
                + "\n- Mata Pelajaran: " + konsultasi.getMapelKonsul().getName()
                + "\n- Topik: " + konsultasi.getTopic();

        List<SiswaKonsultasiModel> listSiswaKonsul = siswaKonsultasiService.getListSiswaByKonsultasi(konsultasi);
        for (SiswaKonsultasiModel siwaKonsul: listSiswaKonsul) {
            emailService.sendEmail(siwaKonsul.getSiswaKonsul().getEmail(), emailSubject, emailBody);
        }

        konsultasi.setStatus(DITOLAK);
        konsultasiService.updateKonsultasi(konsultasi);
        redirectAttributes.addFlashAttribute("errorMessage", "Konsultasi gagal diterima karena bertabrakan dengan jadwal anda\nKonsultasi ini akan otomatis ditolak!");
        return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to failed page

    }

    @GetMapping("/konsultasi/tolak/{idKonsultasi}")
    public String tolakKonsultasi(@PathVariable Integer idKonsultasi, RedirectAttributes redirectAttributes) {
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);
        konsultasi.setStatus(DITOLAK);
        konsultasiService.updateKonsultasi(konsultasi);

        String formattedDate = getFormattedDate(konsultasi.getStartTime().toLocalDate());


        String emailSubject = "PENGAJAR MENOLAK KONSULTASI!";
        String emailBody = "Pengajar telah menolak permintaan konsultasi yang kamu ikuti, mohon cari jadwal konsultasi lain."
                + "\n\nDetail konsultasi"
                + "\n- Tanggal: " + formattedDate
                + "\n- Waktu konsultasi: " + konsultasi.getStartTime().toLocalTime() + " - " + konsultasi.getEndTime().toLocalTime()
                + "\n- Mata Pelajaran: " + konsultasi.getMapelKonsul().getName()
                + "\n- Topik: " + konsultasi.getTopic();

        List<SiswaKonsultasiModel> listSiswaKonsul = siswaKonsultasiService.getListSiswaByKonsultasi(konsultasi);
        for (SiswaKonsultasiModel siwaKonsul: listSiswaKonsul) {
            emailService.sendEmail(siwaKonsul.getSiswaKonsul().getEmail(), emailSubject, emailBody);
        }

        redirectAttributes.addFlashAttribute("message", "Konsultasi berhasil ditolak");
        return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page
    }

    @GetMapping("/konsultasi/ikuti/{idKonsultasi}")
    public String ikutiKonsultasi(Principal principal, @PathVariable Integer idKonsultasi, RedirectAttributes redirectAttributes) {
        SiswaModel siswa = siswaService.findSiswaByEmail(principal.getName());
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);

        if (konsultasiService.getIsSiswaAvailable(siswa, konsultasi)){
            SiswaKonsultasiModel siswaKonsultasi = new SiswaKonsultasiModel();
            siswaKonsultasi.setKonsultasi(konsultasi);
            siswaKonsultasi.setSiswaKonsul(siswa);
            siswaKonsultasiService.createSiswaKonsultasi(siswaKonsultasi);

            String emailPenerima = principal.getName();
            String emailSubject = "KONSULTASI BERHASIL DIIKUTI!";
            String emailBody = "";

            String formattedDate = getFormattedDate(konsultasi.getStartTime().toLocalDate());

            if (konsultasi.getStatus().equals(PENDING)){
                emailBody = "Jika pengajar belum mengkonfimasi sebelum " + formattedDate + " pukul " + konsultasi.getStartTime().minusHours(2).toLocalTime()
                        + " maka konsultasi akan otomatis kadaluarsa";
            } else if (konsultasi.getStatus().equals(DITERIMA)){
                emailBody = "Anda tidak dapat membatalkan konsultasi ini karena pengajar sudah menerima permintaan konsultasi.";
            }

            emailBody += ("\n\nDetail konsultasi"
                    + "\n- Tanggal: " + formattedDate
                    + "\n- Waktu konsultasi: " + konsultasi.getStartTime().toLocalTime() + " - " + konsultasi.getEndTime().toLocalTime()
                    + "\n- Jenjang: " + konsultasi.getJenjangKelas().getDisplayValue()
                    + "\n- Mata Pelajaran: " + konsultasi.getMapelKonsul().getName()
                    + "\n- Topik: " + konsultasi.getTopic());

            emailService.sendEmail(emailPenerima, emailSubject, emailBody);

            redirectAttributes.addFlashAttribute("message", "Konsultasi berhasil diikuti");
            return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page
        }
        redirectAttributes.addFlashAttribute("errorMessage", "konsultasi tidak dapat diikuti diikuti karena bertabrakan dengan jadawal anda");
        return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page
    }


    @GetMapping("/konsultasi/extend/{idKonsultasi}")
    public String extendKonsultasi(Principal principal, @PathVariable Integer idKonsultasi, RedirectAttributes redirectAttributes) {
        SiswaModel siswa = siswaService.findSiswaByEmail(principal.getName());
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);

        KonsultasiModel konsultasiNew = new KonsultasiModel();
        konsultasiNew.setStartTime(konsultasi.getEndTime());
        konsultasiNew.setEndTime(konsultasiNew.getStartTime().plusHours(1));
        konsultasiNew.setDuration(1);

        if (konsultasiService.isInRangeTimeExtend(konsultasi.getStartTime(), konsultasiNew.getEndTime()) && konsultasiService.getIsSiswaAvailable(siswa, konsultasiNew) && konsultasiService.getIsPengajarAvailable(konsultasi.getPengajarKonsul(), konsultasiNew)){
            KonsultasiModel konsultasiUpdated = konsultasiService.getKonsultasi(idKonsultasi);
            konsultasiUpdated.setEndTime(konsultasi.getEndTime().plusHours(1));
            konsultasiUpdated.setDuration(konsultasi.getDuration()+1);
            konsultasiService.updateKonsultasi(konsultasiUpdated);

            redirectAttributes.addFlashAttribute("message", "Durasi konsultasi berhasil diperpanjang");
            return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page
        }
        redirectAttributes.addFlashAttribute("errorMessage", "durasi konsultasi tidak dapat diperpanjang karena waktu tidak tersedia");
        return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page
    }

    private String getFormattedDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        return date.format(formatter);
    }

}

