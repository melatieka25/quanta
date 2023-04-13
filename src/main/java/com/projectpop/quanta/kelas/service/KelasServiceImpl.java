package com.projectpop.quanta.kelas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.kelas.model.JadwalAvail;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.repository.KelasDb;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
@Transactional
public class KelasServiceImpl implements KelasService{
    @Autowired
    KelasDb kelasDb;

    @Override
    public void deleteKelas(KelasModel kelas) {
        kelasDb.delete(kelas);
    }

    @Override
    public void addKelas(KelasModel kelas) {
        kelasDb.save(kelas);

    }
    @Override
    public List<KelasModel> getListKelas(){
        return kelasDb.findAll();
    }

    @Override
    public List<KelasModel> getListKelasByDays(Integer day) {
        List<KelasModel> listKelas = new ArrayList<>();
        if (day == 1 || day == 3 || day == 5) {
            listKelas = kelasDb.findByDays(JadwalAvail.SeninRabuJumat);
        } else if (day == 2|| day == 4 || day==6){
            listKelas = kelasDb.findByDays(JadwalAvail.SelasaKamisSabtu);
        }
        List<KelasModel> res = getListKelasAktif(listKelas);
        return res;
    }

    @Override
    public List<KelasModel> getListKelasAktif(List<KelasModel> listKelas) {
        List<KelasModel> res = new ArrayList<>();
        for (KelasModel kelas : listKelas) {
            if (kelas.getTahunAjar().getIsAktif()) {
                res.add(kelas);
            }
        }
        return res;
    }

    @Override
    public KelasModel getKelasById(Integer id) {
        Optional<KelasModel> kelas = kelasDb.findById(id);
        return kelas.orElse(null);
    }

    @Override
    public List<KelasModel> getKelasByName(String name) {
        List<KelasModel> kelas = kelasDb.findByName(name);
        return kelas;
    }
}
