package com.projectpop.quanta.orangtua.restcontroller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.orangtua.service.OrtuService;
import com.projectpop.quanta.siswa.model.SiswaModel;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ortu")
public class OrtuRestController {

    @Autowired
    OrtuService ortuService;

    @GetMapping("/list-anak/{email}")
    private List<SiswaModel> getListAnak(@PathVariable("email") String email) {
        OrtuModel ortu = ortuService.getOrtuByEmail(email);
        List<SiswaModel> listAnak = new ArrayList<>();
        for (SiswaModel anak : ortu.getListAnak()) {
            if (anak.getIsActive()) {
                listAnak.add(anak);
            }
        }
        return listAnak;
    }
    
}
