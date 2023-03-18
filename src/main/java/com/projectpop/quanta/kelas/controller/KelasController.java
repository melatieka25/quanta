package com.projectpop.quanta.kelas.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("")
    public String viewAllKelas(Model model){
        List<KelasModel> listKelas = kelasService.getAllKelas();

        model.addAttribute("ListKelas", listKelas);
        return "kelas/view-daftar-kelas";
    }

    @GetMapping("/detail/{kelas_id}")
    public String viewDetailKelas(Model model, @PathVariable String kelas_id){
        KelasModel kelas = kelasService.getKelasById(Integer.valueOf(kelas_id));

        model.addAttribute("kelas", kelas);
        return "kelas/detail2";
    }

    @GetMapping("/add")
    public String addKelasFormPage(Model model) {
        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        KelasModel kelas = new KelasModel();
        List<SiswaKelasModel> listSiswaKelasNew = new ArrayList<>();
        kelas.setListSiswaKelas(listSiswaKelasNew);
        kelas.getListSiswaKelas().add(new SiswaKelasModel());

        model.addAttribute("listSiswa", listSiswa);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);
        model.addAttribute("kelas", kelas);
        return "kelas/form-add-kelas";
    }

    @PostMapping(value = "/add", params = {"save"})
    public String addKelasSubmitPage(@ModelAttribute KelasModel kelas, RedirectAttributes redirectAttrs , Model model) {
        if (kelas.getListSiswaKelas() == null) {
            kelas.setListSiswaKelas(new ArrayList<>());
        }
        int index = 0;
        for (SiswaKelasModel siswaKelas : kelas.getListSiswaKelas()) {
            siswaKelas.setKelasSiswa(kelas);
            siswaKelas.setSiswa(kelas.getListSiswaKelas().get(index).getSiswa());
            index++;
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
        kelas.getListSiswaKelas().add(new SiswaKelasModel());

        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        model.addAttribute("listSiswa", listSiswa);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);
        model.addAttribute("kelas", kelas);

        return "kelas/form-add-kelas";
    }

    @PostMapping(value="/add", params={"deleteRowSiswa"})
    private String deleteRowKelasMultiple(
            @ModelAttribute KelasModel kelas,
            @RequestParam("deleteRowSiswa") Integer row,
            Model model
    ){
        final Integer rowId = Integer.valueOf(row);
        kelas.getListSiswaKelas().remove(rowId.intValue());

        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        model.addAttribute("listSiswa", listSiswa);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);
        model.addAttribute("kelas", kelas);

        return "kelas/form-add-kelas";
    }

    @GetMapping("/edit/{id}")
    public String updateKelasForm(@PathVariable String id, Model model) {
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

        model.addAttribute("kelas", kelas);
        model.addAttribute("listSiswa", listSiswa);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);
        return "kelas/form-update-kelas";

    }

    @PostMapping(value="edit", params = {"save"})
    public String updateKelasSubmitPage(@ModelAttribute KelasModel kelas, RedirectAttributes redirectAttrs) {
        KelasModel kelasExs = kelasService.getKelasById(kelas.getId());
        kelasExs.setDays(kelas.getDays());
        kelasExs.setName(kelas.getName());
        kelasExs.setJenjang(kelas.getJenjang());
        kelasExs.setKakakAsuh(kelas.getKakakAsuh());
        kelasExs.setTahunAjar(kelas.getTahunAjar());

        ArrayList<Integer> listIdSiswaLama = new ArrayList<Integer>();
        ArrayList<Integer> listIdSiswaBaru = new ArrayList<Integer>();

        List<SiswaKelasModel> listSiswaKelasUpdated = new ArrayList<SiswaKelasModel>();

        for (SiswaKelasModel siswaKelas : kelas.getListSiswaKelas()){
            listIdSiswaBaru.add(siswaKelas.getSiswa().getId());
        }

        for (SiswaKelasModel siswaKelas : kelasExs.getListSiswaKelas()){
            listIdSiswaLama.add(siswaKelas.getSiswa().getId());
            SiswaModel siswaUpdated = siswaKelas.getSiswa();
            siswaUpdated.getListKelasSiswa().remove(siswaKelas);
        }

        kelasExs.setListSiswaKelas(null);
        siswaKelasService.deleteAllByKelasSiswa(kelasExs);


        for (int idSiswa : listIdSiswaLama){
            if (listIdSiswaBaru.contains(idSiswa)) {
                SiswaKelasModel siswaKelasBaru = new SiswaKelasModel();
                siswaKelasBaru.setKelasSiswa(kelasExs);
                siswaKelasBaru.setSiswa(siswaService.getById(idSiswa));
                listSiswaKelasUpdated.add(siswaKelasBaru);
            }
        }

        for (int idSiswa : listIdSiswaBaru){
            if (!listIdSiswaLama.contains(idSiswa)) {
                SiswaKelasModel siswaKelasBaru = new SiswaKelasModel();
                siswaKelasBaru.setKelasSiswa(kelasExs);
                siswaKelasBaru.setSiswa(siswaService.getById(idSiswa));
                listSiswaKelasUpdated.add(siswaKelasBaru);
            }
        }

        kelasExs.setListSiswaKelas(listSiswaKelasUpdated);

        kelasService.addKelas(kelasExs);

        redirectAttrs.addFlashAttribute("message", "Data kelas " + kelasExs.getName() + " telah berhasil diubah!");
        return "redirect:/kelas/detail/" + kelasExs.getId();
    }

    @GetMapping("/delete/{id}")
    public String DeleteKelasForm (@PathVariable String id, Model model) {
        KelasModel courseDeleted = kelasService.getKelasById(Integer.valueOf(id));

        kelasService.deleteKelas(courseDeleted);

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
        kelas.getListSiswaKelas().add(new SiswaKelasModel());

        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        model.addAttribute("listSiswa", listSiswa);
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
        final Integer rowId = Integer.valueOf(row);
        kelas.getListSiswaKelas().remove(rowId.intValue());

        List<SiswaModel> listSiswa = siswaService.getListSiswa();
        List<TahunAjarModel> listTahunAjar = tahunAjarService.getAllTahunAjar();
        List<PengajarModel> listKakakAsuh = pengajarService.getListKakakAsuh();

        model.addAttribute("listSiswa", listSiswa);
        model.addAttribute("listTahunAjar", listTahunAjar);
        model.addAttribute("listKakakAsuh", listKakakAsuh);
        model.addAttribute("kelas", kelas);

        return "kelas/form-update-kelas";
    }



}
