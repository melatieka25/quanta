package com.projectpop.quanta.presensi.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.kelas.repository.KelasDb;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.presensi.model.PresensiStatus;
import com.projectpop.quanta.presensi.repository.PresensiDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class PresensiServiceImpl implements PresensiService{
    @Autowired
    PresensiDb presensiDb;

    @Autowired
    KelasDb kelasDb;

    @Override
    public PresensiModel getPresensiModelById(Integer id) {
        Optional<PresensiModel> presensi = Optional.ofNullable(presensiDb.findById(id));
        if(presensi.isPresent()){
            return presensi.get();
        }
        else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public List<PresensiModel> getListPresensi() {
        return presensiDb.findAll();
    }

    @Override
    public PresensiModel updatePresensi(PresensiModel presensiModel) {
        presensiDb.save(presensiModel);
        return presensiModel;
    }
    @Override
    public PresensiModel createPresensi(JadwalKelasModel jadwal) {
        PresensiModel presensi = new PresensiModel();
        presensi.setJadwal(jadwal);
        presensi.setStatus(PresensiStatus.KOSONG);
        return presensi;
    }

    @Override
    public void deletePresensi(JadwalKelasModel jadwal) {
        List<PresensiModel> listPresensi = jadwal.getListPresensi();
        // System.out.println(listPresensi);
        for (PresensiModel presensi : listPresensi) {
            presensiDb.delete(presensi);
        }
    }
}
