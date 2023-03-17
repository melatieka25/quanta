package com.projectpop.quanta.kelas.service;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.kelas.repository.KelasDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
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
    public List<KelasModel> getAllKelas(){
        return kelasDb.findAll();
    }

    @Override
    public KelasModel getKelasById(Integer id){
        Optional<KelasModel> kelas = kelasDb.findById(id);
        return kelas.orElse(null);
    }

}
