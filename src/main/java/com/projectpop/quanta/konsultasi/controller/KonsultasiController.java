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
import com.projectpop.quanta.user.model.UserRole;
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
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        konsultasiService.reloadStatus();
        konsultasiService.reloadRequestKonsultasi();
        UserModel user = userService.getUserByEmail(principal.getName());
        String[] listStatus = new String[]{"Semua", "Pending", "Diterima", "Ditolak", "Kadaluarsa", "Ditutup"};
        model.addAttribute("listStatus", listStatus);

        if(user.getRole().toString().equals("ADMIN")){
            List<KonsultasiModel> listKonsultasi = konsultasiService.getListKonsultasi();

            model.addAttribute("listKonsultasi", listKonsultasi);
            return "konsultasi/admin-viewall";
        }

        else if(user.getRole().toString().equals("PENGAJAR")){
            PengajarModel pengajar = pengajarService.getPengajarByEmail(principal.getName());
            List<KonsultasiModel> myListKonsultasi = konsultasiService.getListKonsultasiByUser(pengajar);
            List<KonsultasiModel> listRequestKonsultasi = konsultasiService.getRequestKonsultasi(pengajar);

            model.addAttribute("myListKonsultasi", myListKonsultasi);
            model.addAttribute("listRequestKonsultasi", listRequestKonsultasi);

            return "konsultasi/landing-page-pengajar";

        }

        else if(user.getRole().toString().equals("SISWA")){
            SiswaModel siswa = siswaService.findSiswaByEmail(principal.getName());
            Jenjang jenjang = siswa.getJenjang();
            List<SiswaKonsultasiModel> myListKonsultasi = siswaKonsultasiService.getListKonsultasiBySiswa(siswa);
            List<KonsultasiModel> listRekomendasiKonsultasi = konsultasiService.getRekomendasiKonsultasi(siswa, jenjang);

            model.addAttribute("siswa", siswa);
            model.addAttribute("myListKonsultasi", myListKonsultasi);
            model.addAttribute("listRekomendasiKonsultasi", listRekomendasiKonsultasi);

            return "konsultasi/landing-page-siswa";
        }
        return "error/404";
    }

    @GetMapping("/konsultasi/view/{idKonsultasi}" )
    public String viewDetailKonsultasiPage(@PathVariable Integer idKonsultasi, Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);

        for (SiswaKonsultasiModel siswaKonsultasi: konsultasi.getListSiswaKonsultasi()) {
            KelasModel kelas = siswaService.getKelasBimbel(siswaKonsultasi.getSiswaKonsul());
            siswaKonsultasi.getSiswaKonsul().setKelasBimbel(kelas);
        }

        if(userModel.getRole().toString().equals("PENGAJAR")){
            if (!konsultasi.getPengajarKonsul().equals(userModel)){
                return "error/404";
            }
            boolean isToValidate = false;
            boolean isToClose = false;

            if (konsultasi.getStatus().equals(PENDING)){
                isToValidate = true;
            } else if (konsultasiService.isToClose(konsultasi)) {
                isToClose = true;
            }

            model.addAttribute("isToValidate", isToValidate);
            model.addAttribute("isToClose", isToClose);
        }

        if(userModel.getRole().toString().equals("SISWA")){
            if (null == siswaService.getKelasBimbel(((SiswaModel) userModel))){
                return "error/404";
            }
            else if (!konsultasi.getJenjangKelas().equals(((SiswaModel) userModel).getJenjang())){
                return "error/404";
            }
            boolean isJoinable = false;
            boolean isCancelable = false;
            boolean isExtendable = false;

            KonsultasiModel konsultasiNew = new KonsultasiModel();
            konsultasiNew.setStartTime(konsultasi.getEndTime());
            konsultasiNew.setEndTime(konsultasiNew.getStartTime().plusHours(1));
            konsultasiNew.setDuration(1);

            boolean isNotPartOfKonsul= siswaKonsultasiService.isRekomended((SiswaModel) userModel, konsultasi);

            if (konsultasi.getStatus().equals(PENDING) && !isNotPartOfKonsul){
                isCancelable = true;
            }
            else if (konsultasiService.isInRangeTimeExtend(konsultasi.getStartTime(),konsultasiNew.getEndTime())
                    && konsultasiService.getIsSiswaAvailable((SiswaModel) userModel, konsultasiNew)
                    && konsultasiService.getIsPengajarAvailable(konsultasi.getPengajarKonsul(), konsultasiNew)
                    && !isNotPartOfKonsul){
                isExtendable = true;
            }
            else if (isNotPartOfKonsul
                    && konsultasiService.getIsSiswaAvailable((SiswaModel) userModel, konsultasi)
                    && (konsultasi.getListSiswaKonsultasi().size() < 20)
                    && (konsultasi.getStartTime().minusMinutes(10).isAfter(LocalDateTime.now()))){
                isJoinable = true;
            }

            if (konsultasi.getStatus().equals(DITUTUP)){
                String statusKehadiran = "";
                SiswaKonsultasiModel siswaKonsultasi = siswaKonsultasiService.getBySiswaAndKonsultasi((SiswaModel) userModel, idKonsultasi);
                if (siswaKonsultasi.getIsPresent()){
                    statusKehadiran = "Hadir (" + siswaKonsultasi.getDurasiHadir() + "jam)";
                } else {
                    statusKehadiran = "Tidak Hadir";

                }
                model.addAttribute("statusKehadiran", statusKehadiran);
            }


            model.addAttribute("isCancelable", isCancelable);
            model.addAttribute("isExtendable", isExtendable);
            model.addAttribute("isJoinable", isJoinable);
        }

        model.addAttribute("role", userModel.getRole().toString());
        model.addAttribute("konsultasi", konsultasi);
        model.addAttribute("listSiswaKonsultasi", konsultasi.getListSiswaKonsultasi());

        return "konsultasi/view-detail";
    }


    @GetMapping("/konsultasi/cancel/{idKonsultasi}")
    public String cancelKonsultasiPage(Authentication authentication,
                                       @PathVariable Integer idKonsultasi,
                                       Model model,
                                       RedirectAttributes redirectAttrs, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        SiswaModel siswa = siswaService.findSiswaByEmail(authentication.getName());
        SiswaKonsultasiModel siswaKonsultasi = siswaKonsultasiService.getBySiswaAndKonsultasi(siswa, idKonsultasi);
        KonsultasiModel konsultasiCancel = konsultasiService.getKonsultasi(idKonsultasi);
        if (siswaKonsultasiService.isRekomended(siswa, konsultasiCancel)){
            return "error/404";
        }
        if (null != siswaKonsultasiService.cancelConsultation(siswaKonsultasi)) {
            model.addAttribute("konsultasiCancel", konsultasiCancel);

            List<SiswaKonsultasiModel> allListSiswaKonsultasi = siswaKonsultasiService.getListKonsultasiBySiswa(siswa);
            model.addAttribute("siswa", siswa);
            model.addAttribute("listSiswaKonsultasi", allListSiswaKonsultasi);

            redirectAttrs.addFlashAttribute("message", "Konsultasi berhasil dibatalkan!");

            return "redirect:/konsultasi/view/" + idKonsultasi;

        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Konsultasi gagal dibatalkan!");


            return "redirect:/konsultasi/view/" + idKonsultasi;
        }

    }


    @GetMapping("/konsultasi/add")
    public String addKonsultasiFormPage(Model model, Principal principal, RedirectAttributes redirectAttrs) {
        var userModel = userService.getUserByEmail(principal.getName());
        if (!userModel.getRole().equals(UserRole.SISWA)){
            return "error/404";
        } else if (null == siswaService.getKelasBimbel((SiswaModel) userModel)) {
            redirectAttrs.addFlashAttribute("errorMessage", "Anda tidak dapat membuat konsultasi karena belum memiliki kelas!");
            return "redirect:/konsultasi";
        }
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
        konsultasi.setDibuatOleh(siswa);
        konsultasi.setCreatedTime(LocalDateTime.now());
        konsultasi.setExpiredTime(konsultasi.getStartTime().minusHours(2));

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
        return "redirect:/konsultasi/view/" + konsultasi.getId(); // Redirect to success page
    }

    @PostMapping(value = "/konsultasi/terima/{idKonsultasi}", params = {"save"})
    public String submitFormTerimaKonsultasi(Principal principal,  @PathVariable Integer idKonsultasi, String place, RedirectAttributes redirectAttributes) {
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);
        PengajarModel pengajar = pengajarService.getPengajarByEmail(principal.getName());
        if (!konsultasi.getPengajarKonsul().equals(pengajar)){
            redirectAttributes.addFlashAttribute("errorMessage", "Anda tidak dapat menerima konsultasi ini karena bukan pengajar konsultasi!");
            return "redirect:/konsultasi/view/" + idKonsultasi;
        }

        String formattedDate = getFormattedDate(konsultasi.getStartTime().toLocalDate());

        if (konsultasiService.getIsPengajarAvailable(pengajar, konsultasi)){
            konsultasi.setStatus(DITERIMA);
            konsultasi.setPlace(place);
            konsultasi.setAcceptedTime(LocalDateTime.now());
            konsultasiService.updateKonsultasi(konsultasi);
            konsultasiService.reloadRequestKonsultasi();


            ArrayList<String> emailPenerima = new ArrayList<>();
            String emailSubject = "PENGAJAR TELAH MENERIMA KONSULTASI!";
            String emailBody = "Pembatalan konsultasi sudah tidak dapat dilakukan, mohon ikuti konsultasi sesuai jadwal!"
                    + "\n\nDetail konsultasi"
                    + "\n- Tanggal: " + formattedDate
                    + "\n- Waktu konsultasi: " + konsultasi.getStartTime().toLocalTime() + " - " + konsultasi.getEndTime().toLocalTime()
                    + "\n- Tempat: " + konsultasi.getPlace()
                    + "\n- Mata Pelajaran: " + konsultasi.getMapelKonsul().getName()
                    + "\n- Topik: " + konsultasi.getTopic();

            List<SiswaKonsultasiModel> listSiswaKonsul = konsultasi.getListSiswaKonsultasi();
            for (SiswaKonsultasiModel siwaKonsul: listSiswaKonsul) {
                emailPenerima.add(siwaKonsul.getSiswaKonsul().getEmail());
            }
            emailService.sendEmail(emailPenerima, emailSubject, emailBody);


            redirectAttributes.addFlashAttribute("message", "Konsultasi berhasil diterima");
            return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page
        }

        ArrayList<String> emailPenerima = new ArrayList<>();
        String emailSubject = "PENGAJAR MENOLAK KONSULTASI!";
        String emailBody = "Pengajar telah menolak permintaan konsultasi yang kamu ikuti, mohon cari jadwal konsultasi lain."
                + "\n\nDetail konsultasi"
                + "\n- Tanggal: " + formattedDate
                + "\n- Waktu konsultasi: " + konsultasi.getStartTime().toLocalTime() + " - " + konsultasi.getEndTime().toLocalTime()
                + "\n- Mata Pelajaran: " + konsultasi.getMapelKonsul().getName()
                + "\n- Topik: " + konsultasi.getTopic();

        List<SiswaKonsultasiModel> listSiswaKonsul = konsultasi.getListSiswaKonsultasi();
        for (SiswaKonsultasiModel siwaKonsul: listSiswaKonsul) {
            emailPenerima.add(siwaKonsul.getSiswaKonsul().getEmail());
        }
        emailService.sendEmail(emailPenerima, emailSubject, emailBody);

        konsultasi.setRejectedTime(LocalDateTime.now());
        konsultasi.setRejectionReason("sistem secara otomatis karena bentrok");
        konsultasi.setStatus(DITOLAK);
        konsultasiService.updateKonsultasi(konsultasi);
        redirectAttributes.addFlashAttribute("errorMessage", "Konsultasi gagal diterima karena bertabrakan dengan jadwal anda\nKonsultasi ini akan otomatis ditolak!");
        return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to failed page

    }

    @PostMapping(value = "/konsultasi/tolak/{idKonsultasi}", params = {"save"})
    public String submitFormTolakKonsultasi(Principal principal,  @PathVariable Integer idKonsultasi, String rejectionReason, RedirectAttributes redirectAttributes) {
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);
        PengajarModel pengajar = pengajarService.getPengajarByEmail(principal.getName());

        if (!konsultasi.getPengajarKonsul().equals(pengajar)){
            redirectAttributes.addFlashAttribute("errorMessage", "Anda tidak dapat menutup konsultasi ini karena bukan pengajar konsultasi!");
            return "redirect:/konsultasi/view/" + idKonsultasi;
        }
        konsultasi.setStatus(DITOLAK);
        konsultasi.setRejectionReason("pengajar karena: \n" + rejectionReason);
        konsultasi.setRejectedTime(LocalDateTime.now());
        konsultasiService.updateKonsultasi(konsultasi);

        ArrayList<String> emailPenerima = new ArrayList<>();
        String formattedDate = getFormattedDate(konsultasi.getStartTime().toLocalDate());
        String emailSubject = "PENGAJAR MENOLAK KONSULTASI!";
        String emailBody = "Pengajar telah menolak permintaan konsultasi yang kamu ikuti, mohon cari jadwal konsultasi lain."
                + "\n\nDetail konsultasi"
                + "\n- Tanggal: " + formattedDate
                + "\n- Waktu konsultasi: " + konsultasi.getStartTime().toLocalTime() + " - " + konsultasi.getEndTime().toLocalTime()
                + "\n- Mata Pelajaran: " + konsultasi.getMapelKonsul().getName()
                + "\n- Topik: " + konsultasi.getTopic();

        List<SiswaKonsultasiModel> listSiswaKonsul = konsultasi.getListSiswaKonsultasi();
        for (SiswaKonsultasiModel siwaKonsul: listSiswaKonsul) {
            emailPenerima.add(siwaKonsul.getSiswaKonsul().getEmail());
        }
        emailService.sendEmail(emailPenerima, emailSubject, emailBody);

        redirectAttributes.addFlashAttribute("message", "Konsultasi berhasil ditolak");
        return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page
    }


    @GetMapping("/konsultasi/ikuti/{idKonsultasi}")
    public String ikutiKonsultasi(Principal principal, @PathVariable Integer idKonsultasi, RedirectAttributes redirectAttributes, Model model) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        SiswaModel siswa = siswaService.findSiswaByEmail(principal.getName());
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);
        if (!konsultasi.getJenjangKelas().equals(siswa.getJenjang())){
            return "error/404";
        }
        if ((konsultasi.getListSiswaKonsultasi().size() >= 20)) {
            redirectAttributes.addFlashAttribute("errorMessage", "konsultasi tidak dapat diikuti karena kuota sudah penuh");
            return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page
        }
        if (siswaKonsultasiService.isRekomended((SiswaModel) userModel, konsultasi)
                && konsultasiService.getIsSiswaAvailable((SiswaModel) userModel, konsultasi)
                && (konsultasi.getStartTime().minusMinutes(10).isAfter(LocalDateTime.now()))
        ){
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

        if(siswaKonsultasiService.isRekomended(siswa, konsultasi)){
            return "error/404";
        }

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

    @GetMapping("/konsultasi/close/{idKonsultasi}")
    public String closeKonsultasiFormPage(Model model, Principal principal, @PathVariable Integer idKonsultasi) {
        KonsultasiModel konsultasi = konsultasiService.getKonsultasi(idKonsultasi);
        PengajarModel pengajar = pengajarService.getPengajarByEmail(principal.getName());
        if (!konsultasi.getPengajarKonsul().equals(pengajar)){
            return "error/404";
        }
        if (!konsultasiService.isToClose(konsultasi)){
            return "error/404";
        }

        for (SiswaKonsultasiModel siswaKonsultasi: konsultasi.getListSiswaKonsultasi()) {
            KelasModel kelas = siswaService.getKelasBimbel(siswaKonsultasi.getSiswaKonsul());
            siswaKonsultasi.getSiswaKonsul().setKelasBimbel(kelas);
        }

        List<Integer> listOptionDurasi = new ArrayList<>();
        for (int i = konsultasi.getDuration(); i > 0; i--) {
            listOptionDurasi.add(i);
        }

        model.addAttribute("konsultasi", konsultasi);
        model.addAttribute("listOptionDurasi", listOptionDurasi);
        return "konsultasi/form-presensi-konsultasi";
    }

    @PostMapping(value = "/konsultasi/close/{idKonsultasi}", params = {"save"})
    public String submitCloseKonsultasiFormPage(@PathVariable Integer idKonsultasi,
                                                @ModelAttribute KonsultasiModel konsultasi,
                                                RedirectAttributes redirectAttrs){


        for (SiswaKonsultasiModel siswaKonsultasi: konsultasi.getListSiswaKonsultasi()) {

            SiswaKonsultasiModel siswaKonsultasiUpdated = siswaKonsultasiService.getBySiswaAndKonsultasi(siswaKonsultasi.getSiswaKonsul(), idKonsultasi);

            siswaKonsultasiUpdated.setIsPresent(siswaKonsultasi.getIsPresent());
            if(siswaKonsultasiUpdated.getIsPresent()){
                siswaKonsultasiUpdated.setDurasiHadir(siswaKonsultasi.getDurasiHadir());
            } else {
                siswaKonsultasiUpdated.setDurasiHadir(0);
            }
            siswaKonsultasiService.updateSiswaKonsultasi(siswaKonsultasiUpdated);
        }

        KonsultasiModel konsultasiUpdated = konsultasiService.getKonsultasi(idKonsultasi);
        konsultasiUpdated.setStatus(DITUTUP);
        konsultasiUpdated.setClosedTime(LocalDateTime.now());
        konsultasiService.updateKonsultasi(konsultasiUpdated);


        redirectAttrs.addFlashAttribute("message", "Konsultasi berhasil ditutup");
        return "redirect:/konsultasi/view/" + idKonsultasi; // Redirect to success page


    }

}

