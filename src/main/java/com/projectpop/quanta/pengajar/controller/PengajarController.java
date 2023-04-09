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

import java.time.LocalDate;
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
        return "manajemen-user/form-create-pengajar";
    }

    @PostMapping("/create-pengajar")
    public String addPengajarSubmitPage(@ModelAttribute PengajarModel pengajar, Model model, RedirectAttributes redirectAttrs) {
        pengajar.setRole(UserRole.PENGAJAR);
        UserModel sameEmail = userService.getUserByEmail(pengajar.getEmail());
        String password = PasswordManager.generateCommonTextPassword();
        pengajar.setPassword(password);

        if (sameEmail == null){
            pengajar.setPasswordPertama(password);
            pengajar.setIsActive(true);
            pengajar.setIsPassUpdated(false);
            pengajar.setIsKakakAsuh(false);
            pengajar.setStartDate(LocalDate.now());
            pengajarService.addPengajar(pengajar);
            redirectAttrs.addFlashAttribute("message", "Pengajar dengan nama " + pengajar.getNameEmail() + " dan password " + pengajar.getPasswordPertama() + " telah berhasil ditambahkan!");
            return "redirect:/pengajar/detail/" + pengajar.getId();
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "User dengan email " + pengajar.getEmail() + " sudah pernah ditambahkan sebelumnya. Coba lagi dengan email lain!");
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
        if (pengajar != null){
            String timePattern = "EEE, dd-MMM-yyyy";
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern);
            String dateOfBirth = pengajar.getDob().format(dateTimeFormatter);
            String startDate = pengajar.getStartDate().format(dateTimeFormatter);
            pengajar.setListMapel(pengajarService.getPengajarMapel(pengajar));
            pengajar.setKelasDiasuh(pengajarService.getKelasAsuh(pengajar));
            model.addAttribute("pengajar", pengajar);
            model.addAttribute("dateOfBirth", dateOfBirth);
            model.addAttribute("startDate", startDate);
            return "manajemen-user/detail-pengajar2";
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan id " + id + " tidak ditemukan. Gagal melihat detail pengajar");
            return "redirect:/pengajar";
        }
    }

    @GetMapping("{id}/inactive")
    public String inactivatePengajarFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs){

        PengajarModel pengajar = pengajarService.getPengajarById(id);
        
        
        if (pengajar != null){
            model.addAttribute("pengajar", pengajar);
            pengajar.setKelasDiasuh(pengajarService.getKelasAsuh(pengajar));
            int jumlahKelasAktif = pengajarService.getNumberOfKelasAktif(pengajar);
            int jumlahKonsultasiAktif = pengajarService.getNumberOfKonsultasiAktif(pengajar);

            if (pengajar.getKelasDiasuh().equals("-") && jumlahKelasAktif == 0 && jumlahKonsultasiAktif == 0){
                PengajarModel inactivatedPengajar = pengajarService.inactivePengajar(pengajar);
                redirectAttrs.addFlashAttribute("message", "Pengajar dengan nama " + inactivatedPengajar.getNameEmail() + " berhasil di-nonaktifkan.");
            } else {
                if (!pengajar.getKelasDiasuh().equals("-")){
                    redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan nama " + pengajar.getNameEmail() + " merupakan kakak asuh. Gagal menonaktifkan pengajar.");
                } else if (jumlahKelasAktif > 0){
                    redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan nama " + pengajar.getNameEmail() + " memiliki jadwal kelas yang aktif. Gagal menonaktifkan pengajar.");
                } else if (jumlahKonsultasiAktif > 0){
                    redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan nama " + pengajar.getNameEmail() + " memiliki jadwal konsultasi yang aktif. Gagal menonaktifkan pengajar.");
                } else {
                    redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan nama " + pengajar.getNameEmail() + " tidak dapat dinonaktifkan saat ini. Tunggu beberapa saat dan coba lagi.");
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
                redirectAttrs.addFlashAttribute("message", "Pengajar dengan nama " + activatedPengajar.getNameEmail() + " berhasil diaktifkan kembali.");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan id " + id + " tidak ditemukan. Gagal mengaktifkan pengajar.");
            return "redirect:/pengajar";
        }

        return "redirect:/pengajar/detail/" + id;
    }

    @GetMapping("{id}/update")
    public String updatePengajarFormPage(@PathVariable int id, Model model, RedirectAttributes redirectAttrs){

        PengajarModel pengajar = pengajarService.getPengajarById(id);
        if (pengajar != null){
            model.addAttribute("pengajar", pengajar);
            return "manajemen-user/form-update-pengajar";
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Pengajar dengan id " + id + " tidak ditemukan. Gagal mengupdate pengajar");
            return "redirect:/pengajar";
        }
    }

    @PostMapping("update")
    public String updatePengajarSubmitPage(@ModelAttribute PengajarModel pengajar, Model model, RedirectAttributes redirectAttrs) {
        PengajarModel oldPengajar = pengajarService.getPengajarById(pengajar.getId());
        oldPengajar.setName(pengajar.getName());
        oldPengajar.setNickname(pengajar.getNickname());
        oldPengajar.setKtp(pengajar.getKtp());
        oldPengajar.setStatus(pengajar.getStatus());
        oldPengajar.setLastEdu(pengajar.getLastEdu());
        oldPengajar.setAddress(pengajar.getAddress());
        oldPengajar.setUniversity(pengajar.getUniversity());
        oldPengajar.setPhone_num(pengajar.getPhone_num());
        oldPengajar.setJurusan(pengajar.getJurusan());
        oldPengajar.setPob(pengajar.getPob());
        oldPengajar.setDob(pengajar.getDob());
        oldPengajar.setGender(pengajar.getGender());
        oldPengajar.setReligion(pengajar.getReligion());
        PengajarModel updatedPengajar = pengajarService.updatePengajar(oldPengajar);
        redirectAttrs.addFlashAttribute("message", "Pengajar dengan nama " + updatedPengajar.getNameEmail() + " telah berhasil diubah datanya!");
        return "redirect:/pengajar/detail/" + updatedPengajar.getId();
    }

}
