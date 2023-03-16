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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.siswa.model.Jenjang;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.Gender;
import com.projectpop.quanta.user.model.Religion;
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
        model.addAttribute("listGender", Gender.values());
        model.addAttribute("listReligion", Religion.values());
        model.addAttribute("listJenjang", Jenjang.values());
        model.addAttribute("listWali", ortuService.getListOrtu());
        model.addAttribute("siswa", new SiswaModel());
        return "manajemen-user/form-create-siswa";
    }

    @PostMapping("/create-siswa")
    public String addSiswaSubmitPage(@ModelAttribute SiswaModel siswa, Model model, RedirectAttributes redirectAttrs) {
        siswa.setRole(UserRole.SISWA);
        UserModel sameEmail = userService.getUserByEmail(siswa.getEmail());
        String password = PasswordManager.generateCommonTextPassword();
        siswa.setPassword(password);

        if (sameEmail == null){
            siswa.setPasswordPertama(password);
            siswa.setIsActive(true);
            siswa.setIsPassUpdated(false);
            siswaService.addSiswa(siswa);
            redirectAttrs.addFlashAttribute("message", "Siswa dengan email " + siswa.getEmail() + " dan password " + siswa.getPasswordPertama() + " telah berhasil ditambahkan!");
            if (siswa.getOrtu() != null){
                OrtuModel ortu = siswa.getOrtu();
                if (ortu.getListAnak() == null){
                    ortu.setListAnak(new ArrayList<SiswaModel>());
                }
                ortu.getListAnak().add(siswa);
                return "redirect:/siswa";
            } else {
                return "redirect:/ortu/create-ortu/" + siswa.getId();
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
        String timePattern = "EEE, dd-MMM-yyyy";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern);
        String dateOfBirth = siswa.getDob().format(dateTimeFormatter);
        siswa.setKelasBimbel(siswaService.getKelasBimbel(siswa));
        model.addAttribute("siswa", siswa);
        model.addAttribute("dateOfBirth", dateOfBirth);
        return "manajemen-user/detail-siswa";
    }

    @GetMapping("{id}/inactive")
    public String inactivateSiswaFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs){

        SiswaModel siswa = siswaService.getSiswaById(id);
        
        
        if (siswa != null){
            model.addAttribute("siswa", siswa);
            siswa.setKelasBimbel(siswaService.getKelasBimbel(siswa));

            if (siswa.getKelasBimbel() == null){
                SiswaModel inactivatedSiswa = siswaService.inactiveSiswa(siswa);
                redirectAttrs.addFlashAttribute("message", "Siswa dengan nama " + inactivatedSiswa.getName() + " berhasil di-nonaktifkan.");
            } else {
                redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan nama " + siswa.getName() + " terdaftar pada kelas " + siswa.getKelasBimbel().getName() + ". Gagal menonaktifkan siswa.");
                // } else if (!siswa.getListMapel().equals("-")){
                //     redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan nama " + siswa.getName() + " merupakan siswa di satu atau lebih mata pelajaran. Gagal menonaktifkan siswa.");
                // } 
                // } else {
                //     redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan nama " + siswa.getName() + " tidak dapat dinonaktifkan saat ini. Tunggu beberapa saat dan coba lagi.");
                // }
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
                redirectAttrs.addFlashAttribute("message", "Siswa dengan nama " + activatedSiswa.getName() + " berhasil diaktifkan kembali.");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Siswa dengan id " + id + " tidak ditemukan. Gagal mengaktifkan siswa.");
            return "redirect:/siswa";
        }

        return "redirect:/siswa/detail/" + id;
    }

}
