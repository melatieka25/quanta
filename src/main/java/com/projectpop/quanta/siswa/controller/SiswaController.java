package com.projectpop.quanta.siswa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.model.UserRole;
import com.projectpop.quanta.user.service.UserService;
import com.projectpop.quanta.user.auth.PasswordManager;

import java.util.List;

@Controller
@RequestMapping("/siswa")
public class SiswaController {
    @Qualifier("siswaServiceImpl")
    @Autowired
    private SiswaService siswaService;

    @Autowired
    private UserService userService;

    @GetMapping("/create-siswa")
    public String addSiswaFormPage(Model model) {
        model.addAttribute("siswa", new SiswaModel());
        return "siswa/form-add-siswa";
    }

    @PostMapping("/create-siswa")
    public String addSiswaSubmitPage(@ModelAttribute SiswaModel siswa, Model model, RedirectAttributes redirectAttrs) {
        siswa.setRole(UserRole.PENGAJAR);
        UserModel sameEmail = userService.getUserByEmail(siswa.getEmail());

        if (sameEmail == null){
            if (PasswordManager.validationChecker(siswa.getPassword())){
                siswaService.addSiswa(siswa);
                redirectAttrs.addFlashAttribute("message", "Siswa dengan email " + siswa.getEmail() + " telah berhasil ditambahkan!");
                return "redirect:/siswa";
            } else {
                redirectAttrs.addFlashAttribute("error", "Password tidak mengandung huruf besar/huruf kecil/angka/simbol atau kurang dari 8 karakter.");
                return "redirect:/siswa/create-siswa";
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "User dengan email " + siswa.getEmail() + " sudah pernah ditambahkan sebelumnya. Coba lagi dengan email lain!");
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

}
