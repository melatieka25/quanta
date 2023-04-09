package com.projectpop.quanta.siswa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.model.UserRole;
import com.projectpop.quanta.user.service.UserService;
import com.projectpop.quanta.user.auth.PasswordManager;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/create-siswa")
    public String addSiswaFormPage(Model model) {
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
        String password = PasswordManager.generateCommonTextPassword();
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
                if (ortuSiswa.getListAnak() == null){
                    ortuSiswa.setListAnak(new ArrayList<SiswaModel>());
                }
                ortuSiswa.getListAnak().add(siswa);
                return "redirect:/siswa/detail/" + siswa.getId();
            } else {
                OrtuModel ortu = siswa.getOrtu();
                ortu.setRole(UserRole.ORTU);
                UserModel sameEmailOrtu = userService.getUserByEmail(ortu.getEmail());
                String passwordOrtu = PasswordManager.generateCommonTextPassword();
                ortu.setPassword(passwordOrtu);

                if (sameEmailOrtu == null){
                    ortu.setPasswordPertama(passwordOrtu);
                    ortu.setIsActive(true);
                    ortu.setIsPassUpdated(false);
                    ortuService.addOrtu(ortu);

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
                
                redirectAttrs.addFlashAttribute("message", "Siswa dengan nama " + siswa.getNameEmail() + " dan password " + siswa.getPasswordPertama() + " serta wali dengan nama " + ortu.getNameEmail() + " dan password " + ortu.getPasswordPertama() + " telah berhasil ditambahkan!");
                return "redirect:/siswa/detail/" + siswa.getId();
                
            }
            
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "User dengan email " + siswa.getEmail() + " sudah pernah ditambahkan sebelumnya. Coba lagi dengan email lain!");
            return "redirect:/siswa/create-siswa";
        }
    }

    @GetMapping
    public String listSiswa(Model model) {
        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        for (SiswaModel siswa: listSiswa){
            siswa.setKelasBimbel(siswaService.getKelasBimbel(siswa));
        }
        model.addAttribute("listSiswa", listSiswa);
        return "manajemen-user/list-siswa";
    }


    @GetMapping("/detail/{id}")
    public String detailSiswa(@PathVariable int id, Model model, RedirectAttributes redirectAttrs) {
        SiswaModel siswa = siswaService.getSiswaById(id);
        if (siswa != null){
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
    public String inactivateSiswaFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs){

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
    public String activateSiswaFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs){

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
    public String updateSiswaFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs){

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
    public String updatePengajarSubmitPage(@ModelAttribute SiswaModel siswa, Model model, RedirectAttributes redirectAttrs) {
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

}
