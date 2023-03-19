package com.projectpop.quanta.orangtua.contoller;

import java.time.format.DateTimeFormatter;
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

    @GetMapping("/detail/{id}/{siswaId}")
    public String detailOrtu(@PathVariable int id, @PathVariable int siswaId, Model model, RedirectAttributes redirectAttrs) {
        OrtuModel ortu = ortuService.getOrtuById(id);
        ortu.setAnakAktif(ortuService.getAnakAktif(ortu));
        String timePattern = "EEE, dd-MMM-yyyy";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern);
        String dateOfBirth = ortu.getDob().format(dateTimeFormatter);
        model.addAttribute("ortu", ortu);
        model.addAttribute("dateOfBirth", dateOfBirth);
        model.addAttribute("siswaId", siswaId);
        return "manajemen-user/detail-ortu";
    }

    @GetMapping("{id}/{siswaId}/inactive")
    public String inactivateOrtuFormPage(@PathVariable int id, @PathVariable int siswaId, Model model, RedirectAttributes redirectAttrs){

        OrtuModel ortu = ortuService.getOrtuById(id);
        
        
        if (ortu != null){
            model.addAttribute("ortu", ortu);
            ortu.setAnakAktif(ortuService.getAnakAktif(ortu));

            if (ortu.getAnakAktif().equals("-")){
                OrtuModel inactivatedOrtu = ortuService.inactiveOrtu(ortu);
                redirectAttrs.addFlashAttribute("message", "Wali dengan nama " + inactivatedOrtu.getName() + " berhasil di-nonaktifkan.");
            } else {
                redirectAttrs.addFlashAttribute("errorMessage", "Ortu dengan nama " + ortu.getName() + " masih memiliki anak dengan status aktif. Gagal menonaktifkan ortu.");
            }
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Wali dengan id " + id + " tidak ditemukan. Gagal menonaktifkan wali.");
            return "redirect:/ortu";
        }

        return "redirect:/ortu/detail/" + id + "/" + siswaId;
    }

    @GetMapping("{id}/{siswaId}/active")
    public String activateOrtuFormPage(@PathVariable int id, @PathVariable int siswaId, Model model, RedirectAttributes redirectAttrs){

        OrtuModel ortu = ortuService.getOrtuById(id);
        
        
        if (ortu != null){
            model.addAttribute("ortu", ortu);
            OrtuModel activatedOrtu = ortuService.activeOrtu(ortu);
                redirectAttrs.addFlashAttribute("message", "Wali dengan nama " + activatedOrtu.getName() + " berhasil diaktifkan kembali.");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Wali dengan id " + id + " tidak ditemukan. Gagal mengaktifkan wali.");
            return "redirect:/ortu";
        }

        return "redirect:/ortu/detail/" + id + "/" + siswaId;
    }

    @GetMapping("{id}/{siswaId}/update")
    public String updateOrtuFormPage(@PathVariable int id, @PathVariable int siswaId, Model model, RedirectAttributes redirectAttrs){

        OrtuModel ortu = ortuService.getOrtuById(id);
        if (ortu != null){
            model.addAttribute("ortu", ortu);
            model.addAttribute("listGender", Gender.values());
            model.addAttribute("listReligion", Religion.values());
            model.addAttribute("siswaId", siswaId);

            return "manajemen-user/form-update-ortu";
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Wali dengan id " + id + " tidak ditemukan. Gagal mengupdate wali");
            return "redirect:/ortu";
        }
    }

    @PostMapping("{siswaId}/update")
    public String updateOrtuSubmitPage(@ModelAttribute OrtuModel ortu, @PathVariable int siswaId, Model model, RedirectAttributes redirectAttrs) {
        OrtuModel oldOrtu = ortuService.getOrtuById(ortu.getId());
        oldOrtu.setName(ortu.getName());
        oldOrtu.setNickname(ortu.getNickname());
        oldOrtu.setAddress(ortu.getAddress());
        oldOrtu.setJob(ortu.getJob());
        oldOrtu.setPhone_num(ortu.getPhone_num());
        oldOrtu.setKantor(ortu.getKantor());
        oldOrtu.setPob(ortu.getPob());
        oldOrtu.setDob(ortu.getDob());
        oldOrtu.setGender(ortu.getGender());
        oldOrtu.setReligion(ortu.getReligion());
        OrtuModel updatedOrtu = ortuService.updateOrtu(oldOrtu);
        redirectAttrs.addFlashAttribute("message", "Wali dengan email " + updatedOrtu.getEmail() + " telah berhasil diubah datanya!");
        return "redirect:/ortu/detail/" + updatedOrtu.getId() + "/" + siswaId;
    }
    
}
