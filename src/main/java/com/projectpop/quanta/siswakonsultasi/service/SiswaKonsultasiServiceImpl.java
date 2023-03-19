package com.projectpop.quanta.siswakonsultasi.service;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.repository.KonsultasiDb;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.repository.SiswaDb;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.siswakonsultasi.repository.SiswaKonsultasiDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.projectpop.quanta.konsultasi.model.StatusKonsul.PENDING;


@Service
@Transactional
public class SiswaKonsultasiServiceImpl implements SiswaKonsultasiService{
    @Autowired
    SiswaKonsultasiDb siswaKonsultasiDb;

    @Autowired
    KonsultasiDb konsultasiDb;

    @Autowired
    SiswaDb siswaDb;

    @Override
    public SiswaKonsultasiModel getById(Integer id) {
        return siswaKonsultasiDb.getById(id);
    }

    @Override
    public SiswaKonsultasiModel getBySiswaAndKonsultasi(SiswaModel siswa, Integer idKonsultasi) {
        return siswaKonsultasiDb.getByKonsultasiAndSiswaKonsul(konsultasiDb.getById(idKonsultasi), siswa);
    }

    @Override
    public List<SiswaKonsultasiModel> getListSiswaByKonsultasi(KonsultasiModel konsultasi) {
        return siswaKonsultasiDb.findAllByKonsultasi(konsultasi);
    }

    @Override
    public Integer getJumlahSiswaKonsultasi(KonsultasiModel konsultasi) {
        return siswaKonsultasiDb.findAllByKonsultasi(konsultasi).size();
    }

    @Override
    public List<SiswaKonsultasiModel> getListKonsultasiBySiswa(SiswaModel siswa) {
        return siswaKonsultasiDb.findAllBySiswaKonsul(siswa);
    }

    @Override
    public SiswaKonsultasiModel cancelConsultation(SiswaKonsultasiModel siswaKonsultasi) {

        if (siswaKonsultasi.getKonsultasi().getStatus().equals(PENDING)){
            if (siswaKonsultasiDb.findAllByKonsultasi(siswaKonsultasi.getKonsultasi()).size() > 1){
                siswaKonsultasiDb.delete(siswaKonsultasi);
            } else {
                siswaKonsultasiDb.delete(siswaKonsultasi);
                konsultasiDb.delete(siswaKonsultasi.getKonsultasi());
            }
            return siswaKonsultasi;
        }
            return null;
    }

    @Override
    public SiswaKonsultasiModel createSiswaKonsultasi(SiswaKonsultasiModel siswaKonsultasi) {
        return siswaKonsultasiDb.save(siswaKonsultasi);
    }
}