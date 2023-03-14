package com.projectpop.quanta.kelas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpop.quanta.kelas.model.JadwalAvail;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.repository.KelasDb;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

@Service
@Transactional
public class KelasServiceImpl implements KelasService {
    @Autowired
    KelasDb kelasDb;

    @Override
    public List<KelasModel> getListKelas() {
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
        return listKelas;
    }

    @Override
    public KelasModel getKelasById(Integer id) {
        return kelasDb.findById(id);
    }

}
