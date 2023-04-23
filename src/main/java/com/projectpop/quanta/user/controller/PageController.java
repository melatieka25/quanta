package com.projectpop.quanta.user.controller;

import java.lang.ProcessBuilder.Redirect;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.service.KonsultasiService;
import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.model.UserRole;
import com.projectpop.quanta.user.service.UserService;
import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;

@Controller
public class PageController {

    @Autowired
    ServerProperties serverProperties;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;

    @Autowired
    private JadwalKelasService jadwalKelasService;

    @Autowired
    private KonsultasiService konsultasiService;

    @Autowired
    private OrtuService ortuService;

    
    @RequestMapping("/")
    public String home(Principal principal, Model model) {
        UserModel user = userService.getUserByEmail(principal.getName());
        model.addAttribute("username", user.getName());

        if (user.getRole() == UserRole.SISWA || user.getRole() == UserRole.PENGAJAR) {
            
            List<JadwalKelasModel> listJadwalHariIni = jadwalKelasService.getListJadwalByUser(user);
            model.addAttribute("listJadwalHariIni", listJadwalHariIni);

            List<KonsultasiModel> listKonsulHariIni = konsultasiService.getListKonsultasiByUser(user);
            model.addAttribute("listKonsulHariIni", listKonsulHariIni);
        } else if (user.getRole() == UserRole.ORTU) {
            OrtuModel ortu = ortuService.getOrtuById(user.getId());
            SiswaModel anak = ortuService.getDefaultAnakTerpilih(ortu);

            model.addAttribute("anak", anak);
            return "redirect:/anak/" + anak.getId();

        }

        return "home";
    }


    @RequestMapping("/anak/{id}")
    public String homeOrtu(@PathVariable("id") Integer id, Principal principal, Model model) {
        UserModel user = userService.getUserById(id);
        model.addAttribute("username", user.getName());

        List<JadwalKelasModel> listJadwalHariIni = jadwalKelasService.getListJadwalByUser(user);
        model.addAttribute("listJadwalHariIni", listJadwalHariIni);
        List<KonsultasiModel> listKonsulHariIni = konsultasiService.getListKonsultasiByUser(user);
        model.addAttribute("listKonsulHariIni", listKonsulHariIni);
        model.addAttribute("anak", user);
        return "home";
    }

    

    @RequestMapping("/login")
    public String login(Model model){
        model.addAttribute("port", serverProperties.getPort());
        return "auth/login";
    }

    @GetMapping (value = "/logout")
    public ModelAndView logout(Principal principal) {
        UserModel user = userService.getUserByEmail(principal.getName());
        System.out.println(user.getEmail());
        
        return new ModelAndView("redirect:/logout");
    }


}
