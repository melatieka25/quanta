package com.projectpop.quanta.siswa.siswaRestController;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pesan.model.PesanModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.model.UserModel;
import com.projectpop.quanta.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/siswa")
public class SiswaRestController {
    @Autowired
    SiswaService siswaService;

    @Autowired
    PengajarService pengajarService;

    @Autowired
    UserService userService;

    @GetMapping("/rapor-siswa/{email}")
    private Integer getIdAnak(@PathVariable("email") String email) {
        SiswaModel siswaModel = siswaService.getSiswaByEmail(email);
        return siswaModel.getId();
    }
}
