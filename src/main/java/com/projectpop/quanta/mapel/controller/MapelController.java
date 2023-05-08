package com.projectpop.quanta.mapel.controller;


import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.konsultasi.service.KonsultasiService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MapelService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import com.projectpop.quanta.pengajarmapel.service.PengajarMapelService;
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
@RequestMapping("/mapel")
public class MapelController {
    @Qualifier("mapelServiceImpl")
    @Autowired
    private MapelService mapelService;

    @Qualifier("pengajarServiceImpl")
    @Autowired
    private PengajarService pengajarService;

    @Qualifier("pengajarMapelServiceImpl")
    @Autowired
    private PengajarMapelService pengajarMapelService;

    @Qualifier("jadwalKelasServiceImpl")
    @Autowired
    private JadwalKelasService jadwalKelasService;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserService userService;
    
    @Qualifier("konsultasiServiceImpl")
    @Autowired
    private KonsultasiService konsultasiService;

    @GetMapping("")
    public String viewAllMapel(Model model, Principal principal){
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        List<MataPelajaranModel> listMapel = mapelService.getAllMapel();

        model.addAttribute("listMapel", listMapel);
        return "mapel/daftar-mapel";
    }

    @GetMapping("/detail/{mapel_id}")
    public String viewDetailMapel(Model model, @PathVariable String mapel_id, Principal principal){
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        MataPelajaranModel mapel = mapelService.getMapelById(Integer.valueOf(mapel_id));
        String jenjangMapel = mapelService.getJenjangMapel(mapel);

        if (jenjangMapel.equals("smpsma")){
            jenjangMapel = "SMP & SMA";
        }

        model.addAttribute("mapel", mapel);
        model.addAttribute("jenjangMapel", jenjangMapel.toUpperCase());
        return "mapel/detail";
    }

    @GetMapping("/add")
    public String addMapelFormPage(Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        List<PengajarModel> listpengajar = pengajarService.getListPengajarActive();

        MataPelajaranModel mapel = new MataPelajaranModel();
        List<PengajarMapelModel> listPengajarMapelNew = new ArrayList<>();
        mapel.setListPengajarMapel(listPengajarMapelNew);
        mapel.getListPengajarMapel().add(new PengajarMapelModel());

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("mapel", mapel);
        return "mapel/form-add-mapel";
    }

    @PostMapping(value = "/add", params = {"save"})
    public String addMapelSubmitPage(@ModelAttribute MataPelajaranModel mapel, String jenjangMapel, RedirectAttributes redirectAttrs) {
        if (mapel.getListPengajarMapel() == null || mapel.getListPengajarMapel().size() == 0) {
            mapel.setListPengajarMapel(new ArrayList<>());
        } else {
            Set<Integer> listIdPengajar = new HashSet<Integer>();
            List<PengajarMapelModel> listPengajarMapelUpdated = new ArrayList<PengajarMapelModel>();


            for (PengajarMapelModel pengajarMapel : mapel.getListPengajarMapel()){
                listIdPengajar.add(pengajarMapel.getPengajar().getId());
            }

            int index = 0;
            for (Integer idPengajar : listIdPengajar) {
                PengajarMapelModel pengajarMapel = new PengajarMapelModel();
                pengajarMapel.setMapel(mapel);
                pengajarMapel.setPengajar(pengajarService.getPengajarById(idPengajar));
                listPengajarMapelUpdated.add(pengajarMapel);
            }

            mapel.setListPengajarMapel(listPengajarMapelUpdated);
        }

        mapelService.setJenjangMapel(mapel, jenjangMapel);

        for (MataPelajaranModel mapelDb : mapelService.getAllMapel()){
            if (mapel.getName().equals(mapelDb.getName())){
                redirectAttrs.addFlashAttribute("error", "Mata pelajaran dengan nama " + mapel.getName() + " tidak bisa ditambahkan karena sudah ada di database");
                return "redirect:/mapel/add";
            } else if (mapel.getAbbr().equals(mapelDb.getAbbr())){
                redirectAttrs.addFlashAttribute("error", "Mata pelajaran dengan singkatan " + mapel.getAbbr() + " tidak bisa ditambahkan karena sudah ada di database");
                return "redirect:/mapel/add";
            }
        }

        mapelService.addMapel(mapel);
        redirectAttrs.addFlashAttribute("success", "Mata pelajaran "  +  mapel.getName() + " berhasil ditambahkan");
        return "redirect:/mapel";

    }

    @PostMapping(value="/add", params = {"addRowPengajar"})
    private String addRowMapelMultiple(
            @ModelAttribute MataPelajaranModel mapel,
            Model model
    ){
        if (mapel.getListPengajarMapel() == null || mapel.getListPengajarMapel().size() == 0) {
            mapel.setListPengajarMapel(new ArrayList<>());
        }
        mapel.getListPengajarMapel().add(new PengajarMapelModel());

        List<PengajarModel> listpengajar = pengajarService.getListPengajarActive();
        String jenjangMapel = mapelService.getJenjangMapel(mapel);

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("jenjangMapel", jenjangMapel);
        model.addAttribute("mapel", mapel);
        return "mapel/form-add-mapel";
    }

    @PostMapping(value="/add", params={"deleteRowPengajar"})
    private String deleteRowMapelMultiple(
            @ModelAttribute MataPelajaranModel mapel,
            @RequestParam("deleteRowPengajar") Integer row,
            String jenjangMapel,
            Model model
    ){

        final Integer rowId = Integer.valueOf(row);
        mapel.getListPengajarMapel().remove(rowId.intValue());

        if (mapel.getListPengajarMapel() == null || mapel.getListPengajarMapel().size() == 0) {
            mapel.setListPengajarMapel(new ArrayList<>());
        }

        List<PengajarModel> listpengajar = pengajarService.getListPengajarActive();

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("jenjangMapel", jenjangMapel);;
        model.addAttribute("mapel", mapel);

        return "mapel/form-add-mapel";
    }

    @GetMapping("/edit/{id}")
    public String updateMapelForm(@PathVariable String id, Model model, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        List<PengajarModel> listpengajar = pengajarService.getListPengajarActive();


        MataPelajaranModel mapelExist = mapelService.getMapelById(Integer.valueOf(id));
        var mapel = new MataPelajaranModel();
        mapel.setId(mapelExist.getId());
        mapel.setName(mapelExist.getName());
        mapel.setAbbr(mapelExist.getAbbr());
        mapel.setListPengajarMapel(mapelExist.getListPengajarMapel());

        String jenjangMapel = mapelService.getJenjangMapel(mapelExist);

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("jenjangMapel", jenjangMapel);
        model.addAttribute("mapel", mapel);

        return "mapel/form-update-mapel";

    }

    @PostMapping(value="edit", params = {"save"})
    public String updateMapelSubmitPage(@ModelAttribute MataPelajaranModel mapel, String jenjangMapel, RedirectAttributes redirectAttrs) {
        MataPelajaranModel mapelExs = mapelService.getMapelById(mapel.getId());

        String oldName = mapelExs.getName();

        Set<Integer> listIdPengajarLama = new HashSet<>();
        Set<Integer> listIdPengajarBaru = new HashSet<>();

        List<PengajarMapelModel> listPengajarMapelUpdated = new ArrayList<>();
        mapelService.setJenjangMapel(mapelExs, jenjangMapel);


        for (PengajarMapelModel pengajarMapel : mapelExs.getListPengajarMapel()){
            listIdPengajarLama.add(pengajarMapel.getPengajar().getId());
            PengajarModel pengajarUpdated = pengajarMapel.getPengajar();
            pengajarUpdated.getListPengajarMapel().remove(pengajarMapel);
        }

        for (MataPelajaranModel mapelDb : mapelService.getAllMapel()){
            if (mapel.getName().equals(mapelDb.getName()) && !(mapel.getName().equals(oldName))){
                redirectAttrs.addFlashAttribute("error", "Mapel dengan nama " + mapel.getName() + " tidak bisa ditambahkan karena sudah ada di database");
                return "redirect:/mapel/edit/" + mapelExs.getId();
            }else if (mapel.getAbbr().equals(mapelDb.getAbbr())){
                redirectAttrs.addFlashAttribute("error", "Mata pelajaran dengan singkatan " + mapel.getAbbr() + " tidak bisa ditambahkan karena sudah ada di database");
                return "redirect:/mapel/edit/" + mapelExs.getId();
            }
        }

        mapelExs.setName(mapel.getName());
        mapelExs.setAbbr(mapel.getAbbr());

        if (mapel.getListPengajarMapel() == null || mapel.getListPengajarMapel().size() == 0) {
            mapel.setListPengajarMapel(new ArrayList<>());
            mapelExs.setListPengajarMapel(new ArrayList<>());
            pengajarMapelService.deleteAllByMapel(mapelExs);

        } else {

            for (PengajarMapelModel pengajarMapel : mapel.getListPengajarMapel()){
                listIdPengajarBaru.add(pengajarMapel.getPengajar().getId());
            }

            mapelExs.setListPengajarMapel(null);
            pengajarMapelService.deleteAllByMapel(mapelExs);


            for (int idPengajar : listIdPengajarLama){
                if (listIdPengajarBaru.contains(idPengajar)) {
                    PengajarMapelModel pengajarMapelNew = new PengajarMapelModel();
                    pengajarMapelNew.setMapel(mapelExs);
                    pengajarMapelNew.setPengajar(pengajarService.getPengajarById(idPengajar));
                    listPengajarMapelUpdated.add(pengajarMapelNew);
                }
            }

            for (int idPengajar : listIdPengajarBaru){
                if (!listIdPengajarLama.contains(idPengajar)) {
                    PengajarMapelModel pengajarMapelNew = new PengajarMapelModel();
                    pengajarMapelNew.setMapel(mapelExs);
                    pengajarMapelNew.setPengajar(pengajarService.getPengajarById(idPengajar));
                    listPengajarMapelUpdated.add(pengajarMapelNew);
                }
            }

            mapelExs.setListPengajarMapel(listPengajarMapelUpdated);

        }


        mapelService.addMapel(mapelExs);
        redirectAttrs.addFlashAttribute("success", "Data Mapel " + mapelExs.getName() + " telah berhasil diubah!");
        return "redirect:/mapel/detail/" + mapelExs.getId();
    }

    @PostMapping(value="/edit", params = {"addRowPengajarUpdate"})
    private String addRowMapelMultipleUpdate(
            @ModelAttribute MataPelajaranModel mapel, String jenjangMapel,
            Model model
    ){
        if (mapel.getListPengajarMapel() == null || mapel.getListPengajarMapel().size() == 0) {
            mapel.setListPengajarMapel(new ArrayList<>());
        }
        mapel.getListPengajarMapel().add(new PengajarMapelModel());

        List<PengajarModel> listpengajar = pengajarService.getListPengajarActive();

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("jenjangMapel", jenjangMapel);
        model.addAttribute("mapel", mapel);

        return "mapel/form-update-mapel";
    }

    @PostMapping(value="/edit", params={"deleteRowPengajarUpdate"})
    private String deleteRowMapelMultipleUpdate(
            @ModelAttribute MataPelajaranModel mapel,
            @RequestParam("deleteRowPengajarUpdate") Integer row,
            String jenjangMapel,
            Model model
    ){
        final Integer rowId = Integer.valueOf(row);
        mapel.getListPengajarMapel().remove(rowId.intValue());

        List<PengajarModel> listpengajar = pengajarService.getListPengajarActive();

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("jenjangMapel", jenjangMapel);
        model.addAttribute("mapel", mapel);

        return "mapel/form-update-mapel";

    }

    @GetMapping("/delete/{id}")
    public String DeleteMapelForm (@PathVariable String id, Model model, RedirectAttributes redirectAttrs, Principal principal) {
        var userModel = userService.getUserByEmail(principal.getName());
        pengajarService.checkIsPengajarDanKakakAsuh(userModel,model);
        MataPelajaranModel mapelDeleted = mapelService.getMapelById(Integer.valueOf(id));
        String namaMapel = mapelDeleted.getName();

        if(mapelDeleted.getListPengajarMapel().size() != 0){
            redirectAttrs.addFlashAttribute("error", "Mapel " + mapelDeleted.getName() + " tidak dapat dihapus karena memiliki pengajar");
            return "redirect:/mapel/detail/" + mapelDeleted.getId();
        }

        if (mapelDeleted.getKonsultasi().size() != 0){
            redirectAttrs.addFlashAttribute("error", "Mapel " + mapelDeleted.getName() + " tidak dapat dihapus karena memiliki jadwal konsultasi");
            return "redirect:/mapel/detail/" + mapelDeleted.getId();
        }

        for (JadwalKelasModel jadwalDb : jadwalKelasService.getListJadwalKelas()){
            if (Integer.valueOf(id) == jadwalDb.getMapelJadwal().getId()){
                redirectAttrs.addFlashAttribute("error", "Mapel " + mapelDeleted.getName() + " tidak dapat dihapus karena memiliki jadwal kelas");
                return "redirect:/mapel/detail/" + mapelDeleted.getId();
            }
        }

        mapelService.deleteMapel(mapelDeleted);
        redirectAttrs.addFlashAttribute("success", "Mapel " + namaMapel + " berhasil dihapus");
        return "redirect:/mapel";

    }


}



