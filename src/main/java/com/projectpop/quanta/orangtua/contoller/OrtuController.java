package com.projectpop.quanta.orangtua.contoller;

import java.time.format.DateTimeFormatter;

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

@Controller
@RequestMapping("/ortu")
public class OrtuController {
    @Qualifier("ortuServiceImpl")
    @Autowired
    private OrtuService ortuService;

    @GetMapping("/detail/{id}/{siswaId}")
    public String detailOrtu(@PathVariable int id, @PathVariable int siswaId, Model model, RedirectAttributes redirectAttrs) {
        OrtuModel ortu = ortuService.getOrtuById(id);
        if (ortu != null){
            ortu.setAnakAktif(ortuService.getAnakAktif(ortu));
            String timePattern = "EEE, dd-MMM-yyyy";
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern);
            String dateOfBirth = ortu.getDob().format(dateTimeFormatter);
            model.addAttribute("ortu", ortu);
            model.addAttribute("dateOfBirth", dateOfBirth);
            model.addAttribute("siswaId", siswaId);
            return "manajemen-user/detail-ortu";
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Wali dengan id " + id + " tidak ditemukan. Gagal melihat detail wali");
            return "redirect:/ortu";
        }
    }

    @GetMapping("{id}/{siswaId}/inactive")
    public String inactivateOrtuFormPage(@PathVariable int id, @PathVariable int siswaId, Model model, RedirectAttributes redirectAttrs){

        OrtuModel ortu = ortuService.getOrtuById(id);
        
        
        if (ortu != null){
            model.addAttribute("ortu", ortu);
            ortu.setAnakAktif(ortuService.getAnakAktif(ortu));

            if (ortu.getAnakAktif().equals("-")){
                OrtuModel inactivatedOrtu = ortuService.inactiveOrtu(ortu);
                redirectAttrs.addFlashAttribute("message", "Wali dengan nama " + inactivatedOrtu.getNameEmail() + " berhasil di-nonaktifkan.");
            } else {
                redirectAttrs.addFlashAttribute("errorMessage", "Wali dengan nama " + ortu.getNameEmail() + " masih memiliki anak dengan status aktif. Gagal menonaktifkan ortu.");
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
                redirectAttrs.addFlashAttribute("message", "Wali dengan nama " + activatedOrtu.getNameEmail() + " berhasil diaktifkan kembali.");
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
        redirectAttrs.addFlashAttribute("message", "Wali dengan nama " + updatedOrtu.getNameEmail()+ " telah berhasil diubah datanya!");
        return "redirect:/ortu/detail/" + updatedOrtu.getId() + "/" + siswaId;
    }
    
}
