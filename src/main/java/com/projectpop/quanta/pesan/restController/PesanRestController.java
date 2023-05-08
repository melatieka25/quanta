package com.projectpop.quanta.pesan.restController;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pesan.model.PesanModel;
import com.projectpop.quanta.pesan.service.PesanService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/siswa")
public class PesanRestController {
    @Autowired
    SiswaService siswaService;

    @Autowired
    PengajarService pengajarService;

    @Autowired
    PesanService pesanService;

    @Autowired
    UserService userService;
    @GetMapping("/pesan/{id}")
    private List<PesanModel> getPesan(@PathVariable("id") Integer id){
        UserModel userModel = userService.getUserById(id);
        SiswaModel siswaModel = siswaService.getSiswaById(userModel.getId());
        if(siswaModel != null){
            List<PesanModel> listPesan = pesanService.getPesanBySiswa(siswaModel);
            System.out.println("ini index 0 list pesan " + listPesan.get(0).getMsg());
            return listPesan;
        }
        else{
            return null;
        }
    }
}
