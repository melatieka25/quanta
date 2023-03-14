package com.projectpop.quanta.pengajar.controller;

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

import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.model.UserRole;
import com.projectpop.quanta.user.service.UserService;
import com.projectpop.quanta.user.auth.PasswordManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/pengajar")
public class PengajarController {
    @Qualifier("pengajarServiceImpl")
    @Autowired
    private PengajarService pengajarService;

    @Autowired
    private UserService userService;

    @GetMapping("/create-pengajar")
    public String addPengajarFormPage(Model model) {
        model.addAttribute("pengajar", new PengajarModel());
        return "pengajar/form-add-pengajar";
    }

    @PostMapping("/create-pengajar")
    public String addPengajarSubmitPage(@ModelAttribute PengajarModel pengajar, Model model, RedirectAttributes redirectAttrs) {
        pengajar.setRole(UserRole.PENGAJAR);
        UserModel sameEmail = userService.getUserByEmail(pengajar.getEmail());

        if (sameEmail == null){
            if (PasswordManager.validationChecker(pengajar.getPassword())){
                pengajarService.addPengajar(pengajar);
                redirectAttrs.addFlashAttribute("message", "Pengajar dengan email " + pengajar.getEmail() + " telah berhasil ditambahkan!");
                return "redirect:/pengajar";
            } else {
                redirectAttrs.addFlashAttribute("error", "Password tidak mengandung huruf besar/huruf kecil/angka/simbol atau kurang dari 8 karakter.");
                return "redirect:/pengajar/create-pengajar";
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "User dengan email " + pengajar.getEmail() + " sudah pernah ditambahkan sebelumnya. Coba lagi dengan email lain!");
            return "redirect:/pengajar/create-pengajar";
        }
    }

    @GetMapping
    public String listPengajar(Model model) {
        List<PengajarModel> listPengajar = pengajarService.getListPengajar();
        for (PengajarModel pengajar: listPengajar){
            pengajar.setListMapel(pengajarService.getPengajarMapel(pengajar));
        }
        model.addAttribute("listPengajar", listPengajar);
        return "manajemen-user/list-pengajar";
    }

    @GetMapping("/detail/{id}")
    public String detailPengajar(@PathVariable int id, Model model, RedirectAttributes redirectAttrs) {
        PengajarModel pengajar = pengajarService.getPengajarById(id);
        String timePattern = "EEE, dd-MMM-yyyy";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern);
        String dateOfBirth = pengajar.getDob().format(dateTimeFormatter);
        pengajar.setListMapel(pengajarService.getPengajarMapel(pengajar));
        pengajar.setKelasDiasuh(pengajarService.getKelasAsuh(pengajar));
        model.addAttribute("pengajar", pengajar);
        model.addAttribute("dateOfBirth", dateOfBirth);
        return "manajemen-user/detail-pengajar2";
    }

    @GetMapping("{id}/inactive")
    public String inactivatePengajarFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs){

        PengajarModel pengajar = pengajarService.getPengajarById(id);
        
        
        if (pengajar != null){
            model.addAttribute("pengajar", pengajar);
            pengajar.setKelasDiasuh(pengajarService.getKelasAsuh(pengajar));

            if (pengajar.getKelasDiasuh().equals("-")){
                PengajarModel inactivatedPengajar = pengajarService.inactivePengajar(pengajar);
                redirectAttrs.addFlashAttribute("message", "Pengajar dengan nama " + inactivatedPengajar.getName() + " berhasil di-nonaktifkan.");
            } else {
                if (!pengajar.getKelasDiasuh().equals("-")){
                    redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan nama " + pengajar.getName() + " merupakan kakak asuh. Gagal menonaktifkan pengajar.");
                // } else if (!pengajar.getListMapel().equals("-")){
                //     redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan nama " + pengajar.getName() + " merupakan pengajar di satu atau lebih mata pelajaran. Gagal menonaktifkan pengajar.");
                // } 
                } else {
                    redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan nama " + pengajar.getName() + " tidak dapat dinonaktifkan saat ini. Tunggu beberapa saat dan coba lagi.");
                }
            }
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan id " + id + " tidak ditemukan. Gagal menonaktifkan pengajar.");
            return "redirect:/pengajar";
        }

        return "redirect:/pengajar/detail/" + id;
    }

    @GetMapping("{id}/active")
    public String activatePengajarFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs){

        PengajarModel pengajar = pengajarService.getPengajarById(id);
        
        
        if (pengajar != null){
            model.addAttribute("pengajar", pengajar);
            PengajarModel activatedPengajar = pengajarService.activePengajar(pengajar);
                redirectAttrs.addFlashAttribute("message", "Pengajar dengan nama " + activatedPengajar.getName() + " berhasil diaktifkan kembali.");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan id " + id + " tidak ditemukan. Gagal menonaktifkan pengajar.");
            return "redirect:/pengajar";
        }

        return "redirect:/pengajar/detail/" + id;
    }

}
