package com.projectpop.quanta.jadwalkelas.restcontroller;

import com.projectpop.quanta.mapel.service.MapelService;
import org.springframework.web.bind.annotation.RestController;

import com.projectpop.quanta.jadwalkelas.dto.JadwalKelasDTO;
import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.service.JadwalKelasService;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.service.KelasService;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;

import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pengajarmapel.service.PengajarMapelService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.model.UserRole;
import com.projectpop.quanta.user.service.UserService;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;

import java.util.List;
import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/jadwal-kelas")
public class JadwalKelasRestController {
    @Autowired
    private PengajarMapelService pengajarMapelService;

    @Autowired
    private MapelService mapelService;

    @Autowired
    private PengajarService pengajarService;

    @Autowired
    private UserService userService;

    @Autowired
    private KelasService kelasService;

    @Autowired
    private JadwalKelasService jadwalKelasService;


    @GetMapping("/get-pengajar/mapel/{id}")
    private List<PengajarModel> getListPengajarByMapel(@PathVariable("id") Integer id) {
        MataPelajaranModel mapel = mapelService.getMapelById(id);
        List<PengajarMapelModel> listPMapel = pengajarMapelService.getListPengajarByMapel(mapel);
        
        // get value of pengajar
        List<PengajarModel> listPengajar = new ArrayList<>();
        for (PengajarMapelModel pMapel : listPMapel) {
            if (pMapel.getPengajar().getIsActive()) {
                listPengajar.add(pMapel.getPengajar());
            }
        }
        return listPengajar;
    }

    @GetMapping("/get-mapel/pengajar/{id}")
    private List<MataPelajaranModel> getListMapelByPengajar(@PathVariable("id") Integer id) {
        PengajarModel pengajar = pengajarService.getPengajarById(id);
        List<PengajarMapelModel> listPMapel = pengajarMapelService.getListMapelByPengajar(pengajar);

         // get value of mapel
         List<MataPelajaranModel> listMapel = new ArrayList<>();
         for (PengajarMapelModel pMapel : listPMapel) {
             listMapel.add(pMapel.getMapel());
         }
        return listMapel;
    }

    @GetMapping("/get-kelas/hari/{day}")
    private List<KelasModel> getKelasByDay(@PathVariable("day") Integer day) {
        return kelasService.getListKelasByDays(day);
    }

    @GetMapping("/get-jadwal-kelas/{id}")
    private List<JadwalKelasDTO> getJadwalHomepage(Principal principal, @PathVariable("id") String id) {
        UserModel user = userService.getUserByEmail(principal.getName());
        List<JadwalKelasModel> listJadwal = jadwalKelasService.getListJadwalKelas();

        if (user.getRole() == UserRole.SISWA || user.getRole() == UserRole.PENGAJAR) {
            listJadwal = jadwalKelasService.getListJadwalByUser(user);

        } else if (user.getRole() == UserRole.ORTU) {
            UserModel anak = userService.getUserById(Integer.valueOf(id));
            listJadwal = jadwalKelasService.getListJadwalByUser(anak);

        }

        List<JadwalKelasDTO> json = new ArrayList<>();
        for (JadwalKelasModel jadwal : listJadwal) {
            String url = "http://localhost:8080/jadwal-kelas/" + jadwal.getId();
            JadwalKelasDTO item = new JadwalKelasDTO(jadwal.getMapelJadwal().getName(), jadwal.getStartDateClass(), jadwal.getEndDateClass(),url);
            json.add(item);
        }

        return json;
    }

    
}
