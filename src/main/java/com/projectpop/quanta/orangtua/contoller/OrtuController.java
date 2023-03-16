package com.projectpop.quanta.orangtua.contoller;

import java.util.ArrayList;

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

import com.projectpop.quanta.user.model.Gender;
import com.projectpop.quanta.user.model.Religion;
import com.projectpop.quanta.user.model.UserRole;
import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.service.UserService;
import com.projectpop.quanta.user.auth.PasswordManager;

@Controller
@RequestMapping("/ortu")
public class OrtuController {
    @Qualifier("ortuServiceImpl")
    @Autowired
    private OrtuService ortuService;

    @Autowired
    private UserService userService;

    @Autowired
    private SiswaService siswaService;

    @GetMapping("/create-ortu/{siswaId}")
    public String addOrtuFormPage(Model model, @PathVariable int siswaId) {
        model.addAttribute("listGender", Gender.values());
        model.addAttribute("listReligion", Religion.values());
        model.addAttribute("ortu", new OrtuModel());
        model.addAttribute("siswaId", siswaId);
        return "manajemen-user/form-create-ortu";
    }

    @PostMapping("/create-ortu/{siswaId}")
    public String addOrtuSubmitPage(@ModelAttribute OrtuModel ortu,  @PathVariable int siswaId, Model model, RedirectAttributes redirectAttrs) {
        ortu.setRole(UserRole.ORTU);
        UserModel sameEmail = userService.getUserByEmail(ortu.getEmail());
        String password = PasswordManager.generateCommonTextPassword();
        ortu.setPassword(password);

        if (sameEmail == null){
            ortu.setPasswordPertama(password);
            ortu.setIsActive(true);
            ortu.setIsPassUpdated(false);
            ortuService.addOrtu(ortu);

            ArrayList<SiswaModel> listAnak = new ArrayList<SiswaModel>();
            SiswaModel siswa = siswaService.getSiswaById(siswaId);
            listAnak.add(siswa);
            ortu.setListAnak(listAnak);
            siswa.setOrtu(ortu);
            siswaService.updateSiswa(siswa);

            redirectAttrs.addFlashAttribute("message", "Wali dengan email " + ortu.getEmail() + " dan password " + ortu.getPasswordPertama() + " telah berhasil ditambahkan!");
            return "redirect:/siswa";
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "User dengan email " + ortu.getEmail() + " sudah pernah ditambahkan sebelumnya. Coba lagi dengan email lain!");
            return "redirect:/ortu/create-ortu";
        }
    }
    
}
