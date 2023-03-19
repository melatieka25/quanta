package com.projectpop.quanta.siswakelas.service;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;
import com.projectpop.quanta.siswakelas.repository.SiswaKelasDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SiswaKelasServiceImpl implements SiswaKelasService {
    @Autowired
    SiswaKelasDb siswaKelasDb;

    @Override
    public SiswaKelasModel getSiswaKelasById(Integer id) {
        Optional<SiswaKelasModel> kelas = siswaKelasDb.findById(id);
        return kelas.orElse(null);
    }

    @Override
    public List<SiswaKelasModel> getAllbyKelas(KelasModel kelas) {
        return siswaKelasDb.findAllByKelasSiswa(kelas);
    }

    @Override
    public void deleteAllByKelasSiswa(KelasModel kelas){
        siswaKelasDb.deleteAllByKelasSiswa(kelas);
    };

}
