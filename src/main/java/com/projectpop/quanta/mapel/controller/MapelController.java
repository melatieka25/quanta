package com.projectpop.quanta.mapel.controller;


import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.service.MapelService;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import com.projectpop.quanta.pengajarmapel.service.PengajarMapelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("")
    public String viewAllMapel(Model model){
        List<MataPelajaranModel> listMapel = mapelService.getAllMapel();

        model.addAttribute("listMapel", listMapel);
        return "mapel/daftar-mapel";
    }

    @GetMapping("/detail/{mapel_id}")
    public String viewDetailMapel(Model model, @PathVariable String mapel_id){
        MataPelajaranModel mapel = mapelService.getMapelbyId(Integer.valueOf(mapel_id));

        model.addAttribute("mapel", mapel);
        return "mapel/detail";
    }

    @GetMapping("/add")
    public String addMapelFormPage(Model model) {
        List<PengajarModel> listpengajar = pengajarService.getListPengajar();

        MataPelajaranModel mapel = new MataPelajaranModel();
        List<PengajarMapelModel> listPengajarMapelNew = new ArrayList<>();
        mapel.setListPengajarMapel(listPengajarMapelNew);
        mapel.getListPengajarMapel().add(new PengajarMapelModel());

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("mapel", mapel);
        return "mapel/form-add-mapel";
    }

    @PostMapping(value = "/add", params = {"save"})
    public String addMapelSubmitPage(@ModelAttribute MataPelajaranModel mapel, RedirectAttributes redirectAttrs , Model model) {
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

        mapelService.addMapel(mapel);
        redirectAttrs.addFlashAttribute("success", "Mapel berhasil ditambahkan");
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

        List<PengajarModel> listpengajar = pengajarService.getListPengajar();

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("mapel", mapel);
        return "mapel/form-add-mapel";
    }

    @PostMapping(value="/add", params={"deleteRowPengajar"})
    private String deleteRowMapelMultiple(
            @ModelAttribute MataPelajaranModel mapel,
            @RequestParam("deleteRowPengajar") Integer row,
            Model model
    ){

        final Integer rowId = Integer.valueOf(row);
        mapel.getListPengajarMapel().remove(rowId.intValue());

        if (mapel.getListPengajarMapel() == null || mapel.getListPengajarMapel().size() == 0) {
            mapel.setListPengajarMapel(new ArrayList<>());
        }

        List<PengajarModel> listpengajar = pengajarService.getListPengajar();

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("mapel", mapel);

        return "mapel/form-add-mapel";
    }

    @GetMapping("/edit/{id}")
    public String updateMapelForm(@PathVariable String id, Model model) {
        List<PengajarModel> listpengajar = pengajarService.getListPengajar();


        MataPelajaranModel mapelExist = mapelService.getMapelbyId(Integer.valueOf(id));
        var mapel = new MataPelajaranModel();
        mapel.setId(mapelExist.getId());
        mapel.setName(mapelExist.getName());
        mapel.setAbbr(mapelExist.getAbbr());
        mapel.setListPengajarMapel(mapelExist.getListPengajarMapel());


        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("mapel", mapel);

        return "mapel/form-update-mapel";

    }

    @PostMapping(value="edit", params = {"save"})
    public String updateMapelSubmitPage(@ModelAttribute MataPelajaranModel mapel, RedirectAttributes redirectAttrs) {
        MataPelajaranModel mapelExs = mapelService.getMapelbyId(mapel.getId());

        mapelExs.setName(mapel.getName());
        mapelExs.setAbbr(mapel.getAbbr());

        Set<Integer> listIdPengajarLama = new HashSet<>();
        Set<Integer> listIdPengajarBaru = new HashSet<>();

        List<PengajarMapelModel> listPengajarMapelUpdated = new ArrayList<>();


        for (PengajarMapelModel pengajarMapel : mapelExs.getListPengajarMapel()){
            listIdPengajarLama.add(pengajarMapel.getPengajar().getId());
            PengajarModel pengajarUpdated = pengajarMapel.getPengajar();
            pengajarUpdated.getListPengajarMapel().remove(pengajarMapel);
        }

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

        redirectAttrs.addFlashAttribute("message", "Data Mapel " + mapelExs.getName() + " telah berhasil diubah!");
        return "redirect:/mapel/detail/" + mapelExs.getId();
    }

    @PostMapping(value="/edit", params = {"addRowPengajarUpdate"})
    private String addRowMapelMultipleUpdate(
            @ModelAttribute MataPelajaranModel mapel,
            Model model
    ){
        if (mapel.getListPengajarMapel() == null || mapel.getListPengajarMapel().size() == 0) {
            mapel.setListPengajarMapel(new ArrayList<>());
        }
        mapel.getListPengajarMapel().add(new PengajarMapelModel());

        List<PengajarModel> listpengajar = pengajarService.getListPengajar();

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("mapel", mapel);
        return "mapel/form-update-mapel";
    }

    @PostMapping(value="/edit", params={"deleteRowPengajarUpdate"})
    private String deleteRowMapelMultipleUpdate(
            @ModelAttribute MataPelajaranModel mapel,
            @RequestParam("deleteRowPengajarUpdate") Integer row,
            Model model
    ){
        final Integer rowId = Integer.valueOf(row);
        mapel.getListPengajarMapel().remove(rowId.intValue());

        List<PengajarModel> listpengajar = pengajarService.getListPengajar();

        model.addAttribute("listpengajar", listpengajar);
        model.addAttribute("mapel", mapel);

        return "mapel/form-update-mapel";

    }

    @GetMapping("/delete/{id}")
    public String DeleteMapelForm (@PathVariable String id, Model model) {
        MataPelajaranModel mapelDeleted = mapelService.getMapelbyId(Integer.valueOf(id));

        mapelService.deleteMapel(mapelDeleted);

        return "redirect:/mapel";

    }


}



