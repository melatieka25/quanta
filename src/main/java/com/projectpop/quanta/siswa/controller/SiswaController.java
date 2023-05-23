package com.projectpop.quanta.siswa.controller;

import com.projectpop.quanta.email.service.EmailService;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.service.KelasService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pesan.model.PesanModel;
import com.projectpop.quanta.pesan.service.PesanService;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.presensi.model.PresensiStatus;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.tahunajar.service.TahunAjarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.siswa.model.SiswaCsvModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.model.UserRole;
import com.projectpop.quanta.user.service.UserService;
import com.projectpop.quanta.user.auth.PasswordManager;

import java.security.Principal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Controller
@RequestMapping("/siswa")
public class SiswaController {
    @Qualifier("siswaServiceImpl")
    @Autowired
    private SiswaService siswaService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrtuService ortuService;

    @Autowired
    private TahunAjarService tahunAjarService;

    @Autowired
    private KelasService kelasService;

    @Autowired
    private PengajarService pengajarService;

    @Autowired
    private JadwalKelasService jadwalKelasService;

    @Autowired
    private PesanService pesanService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/create-siswa")
    public String addSiswaFormPage(Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        model.addAttribute("listWali", ortuService.getListOrtu());
        SiswaModel siswa = new SiswaModel();
        OrtuModel ortu = new OrtuModel();
        siswa.setOrtu(ortu);

        model.addAttribute("siswa", siswa);
        return "manajemen-user/form-create-siswa";
    }

    @PostMapping("/create-siswa")
    public String addSiswaSubmitPage(@ModelAttribute SiswaModel siswa, @RequestParam("statusWali") String statusWali, Model model, RedirectAttributes redirectAttrs) {
        siswa.setRole(UserRole.SISWA);
        UserModel sameEmail = userService.getUserByEmail(siswa.getEmail());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMM");
        String formattedDate = siswa.getDob().format(dateFormatter);
        String password = PasswordManager.generateCommonTextPassword(formattedDate);
        siswa.setPassword(password);

        if (sameEmail == null){
            siswa.setPasswordPertama(password);
            siswa.setIsActive(true);
            siswa.setIsPassUpdated(false);
            redirectAttrs.addFlashAttribute("message", "Siswa dengan nama " + siswa.getNameEmail() + " dan password " + siswa.getPasswordPertama() + " telah berhasil ditambahkan!");
            if (statusWali.equals("sudah_terdaftar")){
                OrtuModel ortuSiswa = ortuService.getOrtuById(siswa.getOrtuId());
                siswa.setOrtu(ortuSiswa);
                siswaService.addSiswa(siswa);
                String emailPenerima = siswa.getEmail();
                String emailSubject = "Selamat! Akun QUANTA (Quantum Assistant) Anda Telah Berhasil Dibuat";
                String emailBody = emailService.getCredentialEmailBody(siswa);
                emailService.sendEmail(emailPenerima, emailSubject, emailBody);
                if (ortuSiswa.getListAnak() == null){
                    ortuSiswa.setListAnak(new ArrayList<SiswaModel>());
                }
                ortuSiswa.getListAnak().add(siswa);
                return "redirect:/siswa/detail/" + siswa.getId();
            } else {
                OrtuModel ortu = siswa.getOrtu();
                ortu.setRole(UserRole.ORTU);
                UserModel sameEmailOrtu = userService.getUserByEmail(ortu.getEmail());
                
                String formattedDateOrtu = ortu.getDob().format(dateFormatter);
                String passwordOrtu = PasswordManager.generateCommonTextPassword(formattedDateOrtu);
                ortu.setPassword(passwordOrtu);

                if (sameEmailOrtu == null){
                    ortu.setPasswordPertama(passwordOrtu);
                    ortu.setIsActive(true);
                    ortu.setIsPassUpdated(false);
                    ortuService.addOrtu(ortu);
                    String emailPenerima = ortu.getEmail();
                    String emailSubject = "Selamat! Akun QUANTA (Quantum Assistant) Anda Telah Berhasil Dibuat";
                    String emailBody = emailService.getCredentialEmailBody(ortu);
                    emailService.sendEmail(emailPenerima, emailSubject, emailBody);

                    ArrayList<SiswaModel> listAnak = new ArrayList<SiswaModel>();
                    listAnak.add(siswa);
                    ortu.setListAnak(listAnak);
                    siswa.setOrtu(ortu);
                    siswaService.updateSiswa(siswa);

                } else {
                    redirectAttrs.addFlashAttribute("errorMessage", "User dengan email " + ortu.getEmail() + " sudah pernah ditambahkan sebelumnya. Coba lagi dengan email lain!");
                    return "redirect:/ortu/create-ortu";
                }

                siswaService.addSiswa(siswa);
                String emailPenerima = siswa.getEmail();
                String emailSubject = "Selamat! Akun QUANTA (Quantum Assistant) Anda Telah Berhasil Dibuat";
                String emailBody = emailService.getCredentialEmailBody(siswa);
                emailService.sendEmail(emailPenerima, emailSubject, emailBody);
                
                redirectAttrs.addFlashAttribute("message", "Siswa dengan nama " + siswa.getNameEmail() + " dan password " + siswa.getPasswordPertama() + " serta wali dengan nama " + ortu.getNameEmail() + " dan password " + ortu.getPasswordPertama() + " telah berhasil ditambahkan!");
                return "redirect:/siswa/detail/" + siswa.getId();
                
            }
            
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "User dengan email " + siswa.getEmail() + " sudah pernah ditambahkan sebelumnya. Coba lagi dengan email lain!");
            return "redirect:/siswa/create-siswa";
        }
    }

    @GetMapping
    public String listSiswa(Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        for (SiswaModel siswa: listSiswa){
            siswa.setKelasBimbel(siswaService.getKelasBimbel(siswa));
        }
        model.addAttribute("listSiswa", listSiswa);
        return "manajemen-user/list-siswa";
    }


    @GetMapping("/detail/{id}")
    public String detailSiswa(@PathVariable int id, Model model, RedirectAttributes redirectAttrs, Principal principal) {
        SiswaModel siswa = siswaService.getSiswaById(id);
        if (siswa != null){
            var userModel = userService.getUserByEmail(principal.getName());
            pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
             String timePattern = "EEE, dd-MMM-yyyy";
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern);
            String dateOfBirth = siswa.getDob().format(dateTimeFormatter);
            siswa.setKelasBimbel(siswaService.getKelasBimbel(siswa));
            model.addAttribute("siswa", siswa);
            model.addAttribute("dateOfBirth", dateOfBirth);
            return "manajemen-user/detail-siswa";
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan id " + id + " tidak ditemukan. Gagal melihat detail siswa");
            return "redirect:/siswa";
        }
    }

    @GetMapping("{id}/inactive")
    public String inactivateSiswaFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs, Principal principal){
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);

        SiswaModel siswa = siswaService.getSiswaById(id);


        if (siswa != null){
            model.addAttribute("siswa", siswa);
            siswa.setKelasBimbel(siswaService.getKelasBimbel(siswa));
            int jumlahKonsultasiAktif = siswaService.getNumberOfKonsultasiAktif(siswa);

            if (siswa.getKelasBimbel() == null && jumlahKonsultasiAktif == 0){
                SiswaModel inactivatedSiswa = siswaService.inactiveSiswa(siswa);
                redirectAttrs.addFlashAttribute("message", "Siswa dengan nama " + inactivatedSiswa.getNameEmail() + " berhasil di-nonaktifkan.");
            } else {
                if(siswa.getKelasBimbel() != null){
                    redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan nama " + siswa.getNameEmail() + " terdaftar pada kelas " + siswa.getKelasBimbel().getName() + ". Gagal menonaktifkan siswa.");
                } else if (jumlahKonsultasiAktif > 0){
                redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan nama " + siswa.getNameEmail() + " memiliki jadwal konsultasi yang aktif. Gagal menonaktifkan siswa.");
                } else {
                    redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan nama " + siswa.getNameEmail() + " tidak dapat dinonaktifkan saat ini. Tunggu beberapa saat dan coba lagi.");
                }
            }
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan id " + id + " tidak ditemukan. Gagal menonaktifkan siswa.");
            return "redirect:/siswa";
        }

        return "redirect:/siswa/detail/" + id;
    }

    @GetMapping("{id}/active")
    public String activateSiswaFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs, Principal principal){
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        SiswaModel siswa = siswaService.getSiswaById(id);


        if (siswa != null){

            model.addAttribute("siswa", siswa);
            SiswaModel activatedSiswa = siswaService.activeSiswa(siswa);
                redirectAttrs.addFlashAttribute("message", "Siswa dengan nama " + activatedSiswa.getNameEmail() + " berhasil diaktifkan kembali.");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan id " + id + " tidak ditemukan. Gagal mengaktifkan siswa.");
            return "redirect:/siswa";
        }

        return "redirect:/siswa/detail/" + id;
    }

    @GetMapping("{id}/update")
    public String updateSiswaFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs, Principal principal){
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);

        SiswaModel siswa = siswaService.getSiswaById(id);
        if (siswa != null){
            model.addAttribute("siswa", siswa);

            return "manajemen-user/form-update-siswa";
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan id " + id + " tidak ditemukan. Gagal mengupdate siswa");
            return "redirect:/siswa";
        }
    }

    @PostMapping("update")
    public String updateSiswaSubmitPage(@ModelAttribute SiswaModel siswa, Model model, RedirectAttributes redirectAttrs) {
        SiswaModel oldSiswa = siswaService.getSiswaById(siswa.getId());
        oldSiswa.setName(siswa.getName());
        oldSiswa.setNickname(siswa.getNickname());
        oldSiswa.setJenjang(siswa.getJenjang());
        oldSiswa.setSekolah(siswa.getSekolah());
        oldSiswa.setAddress(siswa.getAddress());
        oldSiswa.setPhone_num(siswa.getPhone_num());
        oldSiswa.setPob(siswa.getPob());
        oldSiswa.setDob(siswa.getDob());
        oldSiswa.setGender(siswa.getGender());
        oldSiswa.setReligion(siswa.getReligion());
        SiswaModel updatedSiswa = siswaService.updateSiswa(oldSiswa);
        redirectAttrs.addFlashAttribute("message", "Siswa dengan nama " + updatedSiswa.getNameEmail() + " telah berhasil diubah datanya!");
        return "redirect:/siswa/detail/" + updatedSiswa.getId();
    }
    @GetMapping("/all-rapor-siswa")
    public String getAllRaporSiswa(Model model, Principal principal){
//        ADMIN
        KelasModel kelasModel;
        Map<SiswaModel,String> mapSiswaModel = new HashMap<>();
        String tahunAjaranNow = tahunAjarService.getTahunAjaranNow();
        List<String> listNamaKelas = new ArrayList<>();
        List<SiswaModel> listAllSiswaActive = siswaService.getListSiswaActive();
        List<KelasModel> listKelasModel = kelasService.getListKelas();
        listNamaKelas.add("Semua");
        for (KelasModel kelas : listKelasModel){
            listNamaKelas.add(kelas.getName());
        }
        for(SiswaModel siswaModel : listAllSiswaActive){
            kelasModel = siswaService.getKelasBimbel(siswaModel);
            if(kelasModel != null){
                mapSiswaModel.put(siswaModel,kelasModel.getName());
            }
            else{
                mapSiswaModel.put(siswaModel,"-");
            }
        }
//        KAKAK ASUH
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        Map<SiswaModel,String> mapSiswaAsuhanModel = new HashMap<>();
        List<String>listNamaKelasAsuhan=new ArrayList<>();
        List<String>listNamaKelasAsuhanFix=new ArrayList<>();
//        var userModel = userService.getUserByEmail(principal.getName());
        PengajarModel pengajarModel = pengajarService.getPengajarById(userModel.getId());
        if (null != pengajarModel){
            listNamaKelasAsuhanFix.add("Semua");
            List<KelasModel> listKelasAsuhan = pengajarModel.getListKelasAsuh();
            for(KelasModel kelasModel1 : listKelasAsuhan){
                listNamaKelasAsuhan.add(kelasModel1.getName());
            }
            for(SiswaModel siswaModel : listAllSiswaActive){
                kelasModel = siswaService.getKelasBimbel(siswaModel);
                if(kelasModel != null){
                    if (listNamaKelasAsuhan.contains(kelasModel.getName())){
                        mapSiswaAsuhanModel.put(siswaModel,kelasModel.getName());
                        if (!listNamaKelasAsuhanFix.contains(kelasModel.getName())){
                            listNamaKelasAsuhanFix.add(kelasModel.getName());
                        }
                    }
                }
            }
            model.addAttribute("mapSiswaAsuhanModel", mapSiswaAsuhanModel);
            model.addAttribute("listNamaKelasAsuhan", listNamaKelasAsuhanFix);
//            model.addAttribute("isKakakAsuh", pengajarModel.getIsKakakAsuh());
        }
        model.addAttribute("tahunAjaranNow", tahunAjaranNow);
        model.addAttribute("mapSiswaModel", mapSiswaModel);
        model.addAttribute("listNamaKelas", listNamaKelas);
        model.addAttribute("listKelasModel", listKelasModel);
        return "rapor-siswa/landing-page";
    }

    @GetMapping(value = "/rapor-siswa/{idSiswa}")
    public String viewRaporSiswa(@PathVariable("idSiswa") Integer idSiswa, Model model, Principal principal){
        String tahunAjaranNow = tahunAjarService.getTahunAjaranNow();
        SiswaModel siswaModel = siswaService.getSiswaById(idSiswa);
        KelasModel kelasnyaSiswa = siswaService.getKelasBimbel(siswaModel);
        List<PresensiModel> listPresensiSiswa = siswaModel.getListPresensiSiswa();
        Map<PresensiModel,String[]> presensiSiswaDanJamMap = new HashMap<>();
        for(PresensiModel presensiModel : listPresensiSiswa){
            String waktu = localDateTimeToDateWithSlash(presensiModel.getJadwal().getStartDateClass());
            String jamStart = localDateTimeToTimeWithSlashNoSeconds(presensiModel.getJadwal().getStartDateClass());
            String jamEnd = localDateTimeToTimeWithSlashNoSeconds(presensiModel.getJadwal().getEndDateClass());
            String tanggalFix = jadwalKelasService.convertMonthNumberToName(waktu);
            presensiSiswaDanJamMap.put(presensiModel,new String[]{tanggalFix,jamStart, jamEnd});
        }
        List<SiswaKonsultasiModel> listKonsultasiSiswa = siswaModel.getListKonsultasiSiswa();
        Integer countDurationKonsul = 0;
        for (SiswaKonsultasiModel siswaKonsultasiModel : listKonsultasiSiswa){
            countDurationKonsul += siswaKonsultasiModel.getKonsultasi().getDuration();
        }
        float persentaseKehadiranKelas = 0;
        float countHadir = 0;
        if (listPresensiSiswa.size() != 0){
            for(PresensiModel presensiModel : listPresensiSiswa){
                if (presensiModel.getStatus().getDisplayValue().equals(PresensiStatus.HADIR.getDisplayValue())){
                    countHadir+=1;
                }
            }
            persentaseKehadiranKelas = (countHadir/listPresensiSiswa.size())*100;
        }
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        OrtuModel ortuModel = ortuService.getOrtuById(userModel.getId());
        if (ortuModel != null){
            List<SiswaModel> listAnak = ortuModel.getListAnak();
            model.addAttribute("listAnak", listAnak);
        }
        Map<PesanModel,String[]> mapPesan = new HashMap<>();
        LinkedHashMap<PesanModel,String[]> linkedHashMapPesan = new LinkedHashMap<PesanModel,String[]>();
        List<PesanModel> listPesan = pesanService.getPesanBySiswa(siswaModel);
        List<PesanModel> listPesanSorted = sortPesanByTime(listPesan);
        for (PesanModel pesanModel : listPesanSorted){
            String tahunSkrg = siswaService.getKelasBimbel(pesanModel.getSiswaPesan()).getTahunAjar().getName();
            String tahunPesan = DateTimeFormatter.ofPattern("yyyy").format(pesanModel.getDateCreated());
            if (tahunSkrg.contains(tahunPesan)){
                if (pesanModel.getUser().getRole().toString().equals("PENGAJAR")){
                    long duration = DAYS.between(pesanModel.getDateCreated(),LocalDateTime.now());
                    linkedHashMapPesan.put(pesanModel,new String[]{"Kakak Asuh", localDateTimeToDateWithSlash(pesanModel.getDateCreated()), localDateTimeToTimeWithSlash(pesanModel.getDateCreated()),Long.toString(duration)});
                    mapPesan.put(pesanModel,new String[]{"Kakak Asuh", localDateTimeToDateWithSlash(pesanModel.getDateCreated()), localDateTimeToTimeWithSlash(pesanModel.getDateCreated()),Long.toString(duration)});
                }
                else if(pesanModel.getUser().getRole().toString().equals("ORTU")){
                    long duration = DAYS.between(pesanModel.getDateCreated(),LocalDateTime.now());
                    linkedHashMapPesan.put(pesanModel,new String[]{"Orang Tua Siswa", localDateTimeToDateWithSlash(pesanModel.getDateCreated()), localDateTimeToTimeWithSlash(pesanModel.getDateCreated()),Long.toString(duration)});
                    mapPesan.put(pesanModel, new String[]{"Orang Tua Siswa", localDateTimeToDateWithSlash(pesanModel.getDateCreated()), localDateTimeToTimeWithSlash(pesanModel.getDateCreated()),Long.toString(duration)});
                }
            }
        }
        PesanModel pesanModel1 = new PesanModel();
        model.addAttribute("pesanModel", pesanModel1);
        model.addAttribute("idSiswa", idSiswa);
        model.addAttribute("sizeMapPesan", linkedHashMapPesan.size());
        model.addAttribute("mapPesan", linkedHashMapPesan);
        String[] listNamaBulan = new String[]{"Semua","Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        model.addAttribute("listNamaBulan", listNamaBulan);
        model.addAttribute("kelasnyaSiswa", kelasnyaSiswa);
        model.addAttribute("tahunAjaranNow", tahunAjaranNow);
        model.addAttribute("siswaModel", siswaModel);
        model.addAttribute("presensiSiswaDanJamMap", presensiSiswaDanJamMap);
        model.addAttribute("countDurationKonsul", countDurationKonsul);
        model.addAttribute("persentaseKehadiranKelas", decfor.format(persentaseKehadiranKelas));
        model.addAttribute("anak", siswaModel);
        return "rapor-siswa/detail-rapor-siswa";
    }
    @GetMapping("/rapor-saya")
    public String viewRaporSaya(Model model, Principal principal){
        String tahunAjaranNow = tahunAjarService.getTahunAjaranNow();
        UserModel user = userService.getUserByEmail(principal.getName());
        if(user.getRole().toString().equals("SISWA")){
            SiswaModel siswaModel = siswaService.findSiswaByEmail(principal.getName());
            KelasModel kelasnyaSiswa = siswaService.getKelasBimbel(siswaModel);
            List<PresensiModel> listPresensiSiswa = siswaModel.getListPresensiSiswa();
            Map<PresensiModel,String[]> presensiSiswaDanJamMap = new HashMap<>();
            for(PresensiModel presensiModel : listPresensiSiswa){
                String waktu = localDateTimeToDateWithSlash(presensiModel.getJadwal().getStartDateClass());
                String jamStart = localDateTimeToTimeWithSlashNoSeconds(presensiModel.getJadwal().getStartDateClass());
                String jamEnd = localDateTimeToTimeWithSlashNoSeconds(presensiModel.getJadwal().getEndDateClass());
                String tanggalFix = jadwalKelasService.convertMonthNumberToName(waktu);
                presensiSiswaDanJamMap.put(presensiModel,new String[]{tanggalFix,jamStart, jamEnd});
            }
            List<SiswaKonsultasiModel> listKonsultasiSiswa = siswaModel.getListKonsultasiSiswa();
            Integer countDurationKonsul = 0;
            for (SiswaKonsultasiModel siswaKonsultasiModel : listKonsultasiSiswa){
//                durasinya tuh dari siswakonsultasi
                countDurationKonsul += siswaKonsultasiModel.getDurasiHadir();
            }
            float persentaseKehadiranKelas = 0;
            float countHadir = 0;
            if (listPresensiSiswa.size() != 0){
                for(PresensiModel presensiModel : listPresensiSiswa){
                    if (presensiModel.getStatus().getDisplayValue().equals(PresensiStatus.HADIR.getDisplayValue())){
                        countHadir+=1;
                    }
                }
                persentaseKehadiranKelas = (countHadir/listPresensiSiswa.size())*100;
            }
            String[] listNamaBulan = new String[]{"Semua","Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
            model.addAttribute("listNamaBulan", listNamaBulan);
            model.addAttribute("kelasnyaSiswa", kelasnyaSiswa);
            model.addAttribute("tahunAjaranNow", tahunAjaranNow);
            model.addAttribute("siswaModel", siswaModel);
            model.addAttribute("presensiSiswaDanJamMap", presensiSiswaDanJamMap);
            model.addAttribute("countDurationKonsul", countDurationKonsul);
            model.addAttribute("persentaseKehadiranKelas", decfor.format(persentaseKehadiranKelas));
            model.addAttribute("anak", siswaModel);
            return "rapor-siswa/detail-rapor-siswa";
        }
        return "";
    }
    @PostMapping(value = "/add/rapor-siswa/{idSiswa}", params = {"save"})
    public String addPesan(@PathVariable("idSiswa") Integer idSiswa, Model model, @ModelAttribute PesanModel pesanModel, RedirectAttributes redirectAttributes, Principal principal){
        UserModel sender = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(sender,model);
        if (!(sender.getRole().toString().equals("PENGAJAR") || sender.getRole().toString().equals("ORTU"))){
            redirectAttributes.addFlashAttribute("message","Anda gagal mengirimkan pesan karena anda bukan kakak asuh atau wali siswa");
            return "redirect:/siswa/rapor-siswa/"+idSiswa;
        }
        else if (sender.getRole().toString().equals("PENGAJAR") && (pengajarService.getPengajarById(sender.getId()).getIsKakakAsuh() == Boolean.FALSE)){
            redirectAttributes.addFlashAttribute("message","Anda gagal mengirimkan pesan karena anda bukan kakak asuh");
            return "redirect:/siswa/rapor-siswa/"+idSiswa;
        }
        else{
            pesanModel.setSiswaPesan(siswaService.getSiswaById(idSiswa));
            pesanModel.setUser(sender);
            pesanModel.setDateCreated(LocalDateTime.now());
            pesanService.createPesanModel(pesanModel);
            redirectAttributes.addFlashAttribute("success", "Pesan berhasil ditambahkan");
        }
        PesanModel pesanModel1 = new PesanModel();
        model.addAttribute("pesanModel", pesanModel1);
        return "redirect:/siswa/rapor-siswa/"+idSiswa;

    }
    public static String localDateTimeToDateWithSlash(LocalDateTime localDateTime) {
            return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDateTime);
    }
    public static String localDateTimeToTimeWithSlash(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(localDateTime);
    }
    public static String localDateTimeToTimeWithSlashNoSeconds(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern("HH:mm").format(localDateTime);
    }
    private static final DecimalFormat decfor = new DecimalFormat("0");
    public List<PesanModel> sortPesanByTime (List<PesanModel> pesanModelList){
        PesanModel temp;
        for (int i = 0; i<pesanModelList.size(); i++){
            for (int j = i+1; j<pesanModelList.size(); j++){
                if(pesanModelList.get(i).getDateCreated().isBefore(pesanModelList.get(j).getDateCreated())){
                    temp = pesanModelList.get(i);
                    pesanModelList.set(i,pesanModelList.get(j));
                    pesanModelList.set(j,temp);
                }
            }
        }
        return pesanModelList;
    }
    @GetMapping("/import-csv")
    public String addSiswaImportCsvPage() {
        return "manajemen-user/form-import-siswa";
    }

    @PostMapping("/import-csv")
    public String ImportCsvStatusPage(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttrs) {

        // validate file
        if (file.isEmpty()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Belum ada berkas CSV dipilih. Harap pilih satu berkas CSV!");
            return "redirect:/siswa/import-csv";
            
        } else {
            // parse CSV file to create a list of `User` objects
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

                // create csv bean reader
                CsvToBean<SiswaCsvModel> csvToBean = new CsvToBeanBuilder(reader)
                        .withType(SiswaCsvModel.class)
                        .withSeparator(';')
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                // convert `CsvToBean` object to list of users
                List<SiswaCsvModel> listSiswaCsv = csvToBean.parse();
                List<SiswaModel> listSiswa = new ArrayList<SiswaModel>();
                List<OrtuModel> listOrtu = new ArrayList<OrtuModel>();

                int counterSiswa = 0;
                int counterOrtu = 0;
                for (int i =0; i < listSiswaCsv.size(); i++){
                    if (siswaService.getSiswaByEmail(listSiswaCsv.get(i).getEmail()) == null){
                        SiswaModel siswa = siswaService.convertSiswaCsv(listSiswaCsv.get(i));
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMM");
                        String formattedDate = siswa.getDob().format(dateFormatter);
                        String password = PasswordManager.generateCommonTextPassword(formattedDate);
                        siswa.setPassword(password);
                        siswa.setPasswordPertama(password);
                        siswaService.addSiswa(siswa);
                        String emailPenerima = siswa.getEmail();
                        String emailSubject = "Selamat! Akun QUANTA (Quantum Assistant) Anda Telah Berhasil Dibuat";
                        String emailBody = emailService.getCredentialEmailBody(siswa);
                        emailService.sendEmail(emailPenerima, emailSubject, emailBody);
                        listSiswa.add(siswa);
                        counterSiswa++;
                        OrtuModel ortu = ortuService.getOrtuByEmail(listSiswaCsv.get(i).getEmailOrtu());
                        if (ortu == null) {
                            ortu = ortuService.convertOrtuCsv(listSiswaCsv.get(i));
                            String formattedDateOrtu = ortu.getDob().format(dateFormatter);
                            String passwordOrtu = PasswordManager.generateCommonTextPassword(formattedDateOrtu);
                            ortu.setPassword(passwordOrtu);
                            ortu.setPasswordPertama(passwordOrtu);
                            ortuService.addOrtu(ortu);
                            siswa.setOrtu(ortu);
                            siswaService.updateSiswa(siswa);
                            listOrtu.add(ortu);
                            counterOrtu++;
                            String emailOrtu = ortu.getEmail();
                            String emailSubjectOrtu = "Selamat! Akun QUANTA (Quantum Assistant) Anda Telah Berhasil Dibuat";
                            String emailBodyOrtu = emailService.getCredentialEmailBody(ortu);
                            emailService.sendEmail(emailOrtu, emailSubjectOrtu, emailBodyOrtu);
                        } else {
                            siswa.setOrtu(ortu);
                            siswaService.updateSiswa(siswa);
                        }
                    }
                }

                // save users list on model
                model.addAttribute("listSiswa", listSiswa);
                model.addAttribute("listOrtu", listOrtu);
                if (counterSiswa == 0 && counterOrtu == 0) {
                    redirectAttrs.addFlashAttribute("errorMessage", "Gagal menambahkan siswa dan wali. Semua siswa beserta wali yang ingin ditambahkan telah tersimpan di sistem QUANTA");
                    return "redirect:/siswa";
                } else {
                    model.addAttribute("message", "Berhasil menambahkan data " + counterSiswa + " orang siswa dan " + counterOrtu + " orang wali");
                }
                
            } catch (Exception ex) {
                // Hashmap to map CSV data to
                // Bean attributes.
                Map<String, String> mapping = new HashMap<String, String>();
                mapping.put("id", "id");
                mapping.put("fullName", "fullName");
                mapping.put("address", "address");
                mapping.put("dob", "dob");
                mapping.put("email", "email");
                mapping.put("gender", "gender");
                mapping.put("nickname", "nickname");
                mapping.put("phone_num", "phone_num");
                mapping.put("pob", "pob");
                mapping.put("religion", "religion");
                mapping.put("jenjang", "jenjang");
                mapping.put("sekolah", "sekolah");
                mapping.put("fullNameOrtu", "fullNameOrtu");
                mapping.put("addressOrtu", "addressOrtu");
                mapping.put("dobOrtu", "dobOrtu");
                mapping.put("emailOrtu", "emailOrtu");
                mapping.put("genderOrtu", "genderOrtu");
                mapping.put("nicknameOrtu", "nicknameOrtu");
                mapping.put("phone_numOrtu", "phone_numOrtu");
                mapping.put("pobOrtu", "pobOrtu");
                mapping.put("religionOrtu", "religionOrtu");
                mapping.put("jobOrtu", "jobOrtu");
                mapping.put("kantorOrtu", "kantorOrtu");
        

                HeaderColumnNameTranslateMappingStrategy<SiswaCsvModel> strategy =
                    new HeaderColumnNameTranslateMappingStrategy<>();
                strategy.setType(SiswaCsvModel.class);
                strategy.setColumnMapping(mapping);
        
                // Create csvtobean and csvreader object
                CSVReader csvReader = null;
                try {
                    Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                    csvReader = new CSVReader(reader);

                    CsvToBean<SiswaCsvModel> csvToBean = new CsvToBeanBuilder<SiswaCsvModel>(csvReader)
                        .withMappingStrategy(strategy)
                        .withSeparator(',')
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                    // convert `CsvToBean` object to list of users
                    List<SiswaCsvModel> listSiswaCsv = csvToBean.parse();
                    List<SiswaModel> listSiswa = new ArrayList<SiswaModel>();
                    List<OrtuModel> listOrtu = new ArrayList<OrtuModel>();

                    int counterSiswa = 0;
                    int counterOrtu = 0;
                    for (int i =0; i < listSiswaCsv.size(); i++){
                        if (siswaService.getSiswaByEmail(listSiswaCsv.get(i).getEmail()) == null){
                            SiswaModel siswa = siswaService.convertSiswaCsv(listSiswaCsv.get(i));
                            
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMM");
                            String formattedDate = siswa.getDob().format(dateFormatter);
                            String password = PasswordManager.generateCommonTextPassword(formattedDate);
                            siswa.setPassword(password);
                            siswa.setPasswordPertama(password);
                            siswaService.addSiswa(siswa);
                            String emailPenerima = siswa.getEmail();
                            String emailSubject = "Selamat! Akun QUANTA (Quantum Assistant) Anda Telah Berhasil Dibuat";
                            String emailBody = emailService.getCredentialEmailBody(siswa);
                            emailService.sendEmail(emailPenerima, emailSubject, emailBody);
                            listSiswa.add(siswa);
                            counterSiswa++;
                            OrtuModel ortu = ortuService.getOrtuByEmail(listSiswaCsv.get(i).getEmailOrtu());
                            if (ortu == null) {
                                ortu = ortuService.convertOrtuCsv(listSiswaCsv.get(i));
                                String formattedDateOrtu = ortu.getDob().format(dateFormatter);
                                String passwordOrtu = PasswordManager.generateCommonTextPassword(formattedDateOrtu);
                                ortu.setPassword(passwordOrtu);
                                ortu.setPasswordPertama(passwordOrtu);
                                ortuService.addOrtu(ortu);
                                siswa.setOrtu(ortu);
                                siswaService.updateSiswa(siswa);
                                counterOrtu++;
                                listOrtu.add(ortu);
                                String emailOrtu = ortu.getEmail();
                                String emailBodyOrtu = emailService.getCredentialEmailBody(ortu);
                                emailService.sendEmail(emailOrtu, emailSubject, emailBodyOrtu);
                            }
                        }
                    }

                    // save users list on model
                    model.addAttribute("listSiswa", listSiswa);
                    model.addAttribute("listOrtu", listOrtu);
                    if (counterSiswa == 0 && counterOrtu == 0) {
                        redirectAttrs.addFlashAttribute("errorMessage", "Gagal menambahkan siswa dan wali. Semua siswa beserta wali yang ingin ditambahkan telah tersimpan di sistem QUANTA");
                        return "redirect:/siswa";
                    } else {
                        model.addAttribute("message", "Berhasil menambahkan data " + counterSiswa + " orang siswa dan " + counterOrtu + " orang wali");
                    }
                }
                catch (Exception e) {
                    redirectAttrs.addFlashAttribute("errorMessage", "Berkas yang dimasukkan tidak sesuai. Harap masukkan berkas yang sesuai untuk mengimpor data siswa!");
                    return "redirect:/siswa/import-csv";
                }
                
            }
        }

        return "manajemen-user/list-siswa-imported";
    }
}
