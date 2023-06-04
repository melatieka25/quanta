package com.projectpop.quanta.kelas.controller;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.service.KelasService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;
import com.projectpop.quanta.siswakelas.service.SiswaKelasService;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import com.projectpop.quanta.tahunajar.service.TahunAjarService;
import com.projectpop.quanta.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/kelas")
public class KelasController {
    @Qualifier("kelasServiceImpl")
    @Autowired
    private KelasService kelasService;

    @Qualifier("siswaServiceImpl")
    @Autowired
    private SiswaService siswaService;

    @Qualifier("siswaKelasServiceImpl")
    @Autowired
    private SiswaKelasService siswaKelasService;

    @Qualifier("tahunAjarServiceImpl")
    @Autowired
    private TahunAjarService tahunAjarService;

    @Qualifier("pengajarServiceImpl")
    @Autowired
    private PengajarService pengajarService;

    @Qualifier("jadwalKelasServiceImpl")
    @Autowired
    private JadwalKelasService jadwalKelasService;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;


    @GetMapping("")
    public String viewAllKelas(Model model, Principal principal){
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        List<KelasModel> listKelas = kelasService.getListKelas();

        model.addAttribute("ListKelas", listKelas);
        return "kelas/view-daftar-kelas";
    }

    @GetMapping("/detail/{kelas_id}")
    public String viewDetailKelas(Model model, @PathVariable String kelas_id, Principal principal){
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        KelasModel kelas = kelasService.getKelasById(Integer.valueOf(kelas_id));
        System.out.println(kelas.getListSiswaKelas());
        model.addAttribute("kelas", kelas);
        return "kelas/detail2";
    }

    @GetMapping("/add")
    public String addKelasFormPage(Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        KelasModel kelas = new KelasModel();
        List<SiswaKelasModel> listSiswaKelasNew = new ArrayList<>();
        kelas.setListSiswaKelas(listSiswaKelasNew);
        kelas.getListSiswaKelas().add(new SiswaKelasModel());

        for (int i = 0; i < listSiswa.size(); i++){
            SiswaModel siswaItr = listSiswa.get(i);
            if (siswaService.getKelasBimbel(siswaItr) != null){
                listSiswa.remove(siswaItr);
                i--;
            }
        }

        model.addAttribute("listSiswa", listSiswa);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);
        model.addAttribute("kelas", kelas);
        return "kelas/form-add-kelas";
    }

    @PostMapping(value = "/add", params = {"save"})
    public String addKelasSubmitPage(@ModelAttribute KelasModel kelas, RedirectAttributes redirectAttrs , Model model) {
        List<KelasModel> kelasCheck = kelasService.getKelasByName(kelas.getName());
        if (kelasCheck != null){
            for(KelasModel kelasItr : kelasCheck){
                if (kelasItr.getTahunAjar().getId().equals(kelas.getTahunAjar().getId())){
                    redirectAttrs.addFlashAttribute("error", "Kelas " + kelas.getName() + " sudah ada di database.");
                    return "redirect:/kelas";
                }
            }
        }

        if (kelas.getListSiswaKelas() == null ||kelas.getListSiswaKelas().size() == 0) {
            kelas.setListSiswaKelas(new ArrayList<>());
        } else {
            Set<Integer> listIdSiswa = new HashSet<>();
            List<SiswaKelasModel> listSiswaKelasUpdated = new ArrayList<>();


            for (SiswaKelasModel siswaKelas : kelas.getListSiswaKelas()){
                listIdSiswa.add(siswaKelas.getSiswa().getId());
            }

            for (Integer idSiswa : listIdSiswa) {
                SiswaKelasModel siswaKelas = new SiswaKelasModel();
                siswaKelas.setKelasSiswa(kelas);
                siswaKelas.setSiswa(siswaService.getSiswaById(idSiswa));
                listSiswaKelasUpdated.add(siswaKelas);
            }

            kelas.setListSiswaKelas(listSiswaKelasUpdated);
        }

        if (kelas.getJenjang().getDisplayValue().contains("SMP")){
            kelas.setIsSMP(true);
        } else {
            kelas.setIsSMA(true);
        }


        kelasService.addKelas(kelas);
        redirectAttrs.addFlashAttribute("success", "Kelas berhasil ditambahkan");
        return "redirect:/kelas";

    }

    @PostMapping(value="/add", params = {"addRowSiswa"})
    private String addRowKelasMultiple(
            @ModelAttribute KelasModel kelas,
            Model model
    ){
        if (kelas.getListSiswaKelas() == null || kelas.getListSiswaKelas().size() == 0) {
            kelas.setListSiswaKelas(new ArrayList<>());
        }

        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        List<SiswaModel> listSiswaClean = siswaService.getListSiswaExsAndNoClass(listSiswa, kelas);

        kelas.getListSiswaKelas().add(new SiswaKelasModel());

        model.addAttribute("kelas", kelas);
        model.addAttribute("listSiswa", listSiswaClean);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);

        return "kelas/form-add-kelas";
    }

    @PostMapping(value="/add", params={"deleteRowSiswa"})
    private String deleteRowKelasMultiple(
            @ModelAttribute KelasModel kelas,
            @RequestParam("deleteRowSiswa") Integer row,
            Model model
    ){
        final Integer rowId = row;
        kelas.getListSiswaKelas().remove(rowId.intValue());

        if (kelas.getListSiswaKelas() == null || kelas.getListSiswaKelas().size() == 0) {
            kelas.setListSiswaKelas(new ArrayList<>());
        }

        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        List<SiswaModel> listSiswaClean = siswaService.getListSiswaExsAndNoClass(listSiswa, kelas);

        model.addAttribute("kelas", kelas);
        model.addAttribute("listSiswa", listSiswaClean);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);

        return "kelas/form-add-kelas";
    }

    @GetMapping("/edit/{id}")
    public String updateKelasForm(@PathVariable String id, Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        KelasModel kelasExist = kelasService.getKelasById(Integer.valueOf(id));
        var kelas = new KelasModel();
        kelas.setId(kelasExist.getId());
        kelas.setDays(kelasExist.getDays());
        kelas.setName(kelasExist.getName());
        kelas.setJenjang(kelasExist.getJenjang());
        kelas.setKakakAsuh(kelasExist.getKakakAsuh());
        kelas.setTahunAjar(kelasExist.getTahunAjar());
        kelas.setListSiswaKelas(kelasExist.getListSiswaKelas());

        List<SiswaModel> listSiswaClean = siswaService.getListSiswaExsAndNoClass(listSiswa, kelas);

        model.addAttribute("kelas", kelas);
        model.addAttribute("listSiswa", listSiswaClean);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);
        return "kelas/form-update-kelas";

    }

    @PostMapping(value="edit", params = {"save"})
    public String updateKelasSubmitPage(@ModelAttribute KelasModel kelas, RedirectAttributes redirectAttrs) {
        KelasModel kelasById = kelasService.getKelasById(kelas.getId());

        if (!kelas.getName().equals(kelasById.getName())){
            List<KelasModel> kelasCheck = kelasService.getKelasByName(kelas.getName());
            if (kelasCheck != null){
                for(KelasModel kelasItr : kelasCheck){
                    if (kelasItr.getTahunAjar().getId().equals(kelas.getTahunAjar().getId())){
                        redirectAttrs.addFlashAttribute("error", "Kelas " + kelas.getName() + " sudah ada di database.");
                        redirectAttrs.addFlashAttribute("errorBold", "Perubahan data gagal!");
                        return "redirect:/kelas/detail/" + kelas.getId();
                    }
                }
            }
        }

        KelasModel kelasExs = kelasService.getKelasById(kelas.getId());
        kelasExs.setDays(kelas.getDays());
        kelasExs.setName(kelas.getName());
        kelasExs.setJenjang(kelas.getJenjang());
        kelasExs.setKakakAsuh(kelas.getKakakAsuh());
        kelasExs.setTahunAjar(kelas.getTahunAjar());

        if (kelas.getJenjang().getDisplayValue().contains("SMP")){
            kelasExs.setIsSMP(true);
        } else {
            kelasExs.setIsSMA(true);
        }

        Set<Integer> listIdSiswaLama = new HashSet<>();
        Set<Integer> listIdSiswaBaru = new HashSet<>();

        List<SiswaKelasModel> listSiswaKelasUpdated = new ArrayList<>();

        for (SiswaKelasModel siswaKelas : kelasExs.getListSiswaKelas()){
            listIdSiswaLama.add(siswaKelas.getSiswa().getId());
            SiswaModel siswaUpdated = siswaKelas.getSiswa();
            siswaUpdated.getListKelasSiswa().remove(siswaKelas);
        }

        if (kelas.getListSiswaKelas() == null || kelas.getListSiswaKelas().size() == 0) {
            kelas.setListSiswaKelas(new ArrayList<>());
            kelasExs.setListSiswaKelas(new ArrayList<>());
            siswaKelasService.deleteAllByKelasSiswa(kelasExs);
        } else {
            for (SiswaKelasModel siswaKelas : kelas.getListSiswaKelas()){
                listIdSiswaBaru.add(siswaKelas.getSiswa().getId());
            }

            kelasExs.setListSiswaKelas(null);
            siswaKelasService.deleteAllByKelasSiswa(kelasExs);


            for (int idSiswa : listIdSiswaLama){
                if (listIdSiswaBaru.contains(idSiswa)) {
                    SiswaKelasModel siswaKelasBaru = new SiswaKelasModel();
                    siswaKelasBaru.setKelasSiswa(kelasExs);
                    siswaKelasBaru.setSiswa(siswaService.getSiswaById(idSiswa));
                    listSiswaKelasUpdated.add(siswaKelasBaru);
                }
            }

            for (int idSiswa : listIdSiswaBaru){
                if (!listIdSiswaLama.contains(idSiswa)) {
                    SiswaKelasModel siswaKelasBaru = new SiswaKelasModel();
                    siswaKelasBaru.setKelasSiswa(kelasExs);
                    siswaKelasBaru.setSiswa(siswaService.getSiswaById(idSiswa));
                    listSiswaKelasUpdated.add(siswaKelasBaru);
                }
            }

            kelasExs.setListSiswaKelas(listSiswaKelasUpdated);
        }

        kelasService.addKelas(kelasExs);

        redirectAttrs.addFlashAttribute("success", "Data Kelas " + kelasExs.getName() + " telah berhasil diubah.");
        return "redirect:/kelas/detail/" + kelasExs.getId();
    }

    @GetMapping("/delete/{id}")
    public String DeleteKelasForm (@PathVariable String id, Model model, RedirectAttributes redirectAttrs, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        KelasModel courseDeleted = kelasService.getKelasById(Integer.valueOf(id));

        List<JadwalKelasModel> listJadwalKelas = jadwalKelasService.getListJadwalKelasByKelas(courseDeleted);
        List<SiswaKelasModel> listSiswaKelas = courseDeleted.getListSiswaKelas();

        if (listJadwalKelas.size() != 0){
            redirectAttrs.addFlashAttribute("error", "Kelas memiliki jadwal pembelajaran");
            redirectAttrs.addFlashAttribute("errorBold", "Kelas Gagal dihapus! ");
            return "redirect:/kelas/detail/{id}";

        }

        if (listSiswaKelas.size() != 0){
            redirectAttrs.addFlashAttribute("error", "Kelas memiliki siswa");
            redirectAttrs.addFlashAttribute("errorBold", "Kelas Gagal dihapus! ");
            return "redirect:/kelas/detail/{id}";
        }

        kelasService.deleteKelas(courseDeleted);
        redirectAttrs.addFlashAttribute("success", "Kelas berhasil dihapus!");
        return "redirect:/kelas";

    }

    @PostMapping(value="/edit", params = {"addRowSiswaUpdate"})
    private String addRowKelasMultipleUpdate(
            @ModelAttribute KelasModel kelas,
            Model model
    ){
        if (kelas.getListSiswaKelas() == null || kelas.getListSiswaKelas().size() == 0) {
            kelas.setListSiswaKelas(new ArrayList<>());
        }

        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        List<SiswaModel> listSiswaClean = siswaService.getListSiswaExsAndNoClass(listSiswa, kelas);

        kelas.getListSiswaKelas().add(new SiswaKelasModel());

        model.addAttribute("listSiswa", listSiswaClean);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);
        model.addAttribute("kelas", kelas);

        return "kelas/form-update-kelas";
    }

    @PostMapping(value="/edit", params={"deleteRowSiswaUpdate"})
    private String deleteRowSiswaMultiple(
            @ModelAttribute KelasModel kelas,
            @RequestParam("deleteRowSiswaUpdate") Integer row,
            Model model
    ){
        final Integer rowId = row;
        kelas.getListSiswaKelas().remove(rowId.intValue());

        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        List<SiswaModel> listSiswaClean = siswaService.getListSiswaExsAndNoClass(listSiswa, kelas);

        model.addAttribute("listSiswa", listSiswaClean);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);
        model.addAttribute("kelas", kelas);

        return "kelas/form-update-kelas";
    }



}
