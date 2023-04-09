package com.projectpop.quanta.user.controller;
import java.security.Principal;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.model.UserRole;
import com.projectpop.quanta.user.service.UserService;

@Controller
public class UserController {

    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;

    @Autowired
    private SiswaService siswaService;

    @Autowired
    private OrtuService ortuService;

    @Autowired
    private PengajarService pengajarService;


    @GetMapping("/profil")
    public String profilPengguna(Principal principal, Model model, RedirectAttributes redirectAttrs) {
        UserModel user = userService.getUserByEmail(principal.getName());
        String timePattern = "EEE, dd-MMM-yyyy";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern);
        String dateOfBirth = user.getDob().format(dateTimeFormatter);
        model.addAttribute("dateOfBirth", dateOfBirth);

        if (user.getRole() == UserRole.SISWA) {
            SiswaModel siswa = siswaService.getSiswaById(user.getId());
            siswa.setKelasBimbel(siswaService.getKelasBimbel(siswa));
            model.addAttribute("siswa", siswa);
            return "akun-saya/profil-siswa";
        } else if (user.getRole() == UserRole.PENGAJAR) {
            PengajarModel pengajar = pengajarService.getPengajarById(user.getId());
            String startDate = pengajar.getStartDate().format(dateTimeFormatter);
            pengajar.setListMapel(pengajarService.getPengajarMapel(pengajar));
            pengajar.setKelasDiasuh(pengajarService.getKelasAsuh(pengajar));
            model.addAttribute("pengajar", pengajar);
            model.addAttribute("startDate", startDate);
            return "akun-saya/profil-pengajar2";
        } else if (user.getRole() == UserRole.ORTU) {
            OrtuModel ortu = ortuService.getOrtuById(user.getId());
            ortu.setAnakAktif(ortuService.getAnakAktif(ortu));
            model.addAttribute("ortu", ortu);
            model.addAttribute("dateOfBirth", dateOfBirth);
            return "akun-saya/profil-ortu";
        } else {
            return "home";
        }
        
    }
}
