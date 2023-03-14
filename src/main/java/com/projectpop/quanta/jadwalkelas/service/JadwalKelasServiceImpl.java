package com.projectpop.quanta.jadwalkelas.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.repository.JadwalKelasDb;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.presensi.service.PresensiService;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswajadwalkelas.model.SiswaJadwalModel;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


@Service
@Transactional
public class JadwalKelasServiceImpl implements JadwalKelasService{
    @Autowired
    JadwalKelasDb jadwalKelasDb;

    @Autowired
    PresensiService presensiService;

    @Override
    public JadwalKelasModel getJadwalKelasById(Integer id) {
        return jadwalKelasDb.findById(id);
    }

    @Override
    public List<JadwalKelasModel> getListJadwalKelas() {
        return jadwalKelasDb.findAll();
    }

    public void setListPresensi(JadwalKelasModel jadwalKelas) {
        List<PresensiModel> listPresensi = new ArrayList<>();
        
        // create presensi per student
        List<SiswaKelasModel> listSiswa = jadwalKelas.getKelas().getListSiswaKelas();
        for (SiswaKelasModel siswa : listSiswa) {
            PresensiModel presensi = presensiService.createPresensi(jadwalKelas);
            presensi.setSiswa(siswa.getSiswa());
            listPresensi.add(presensi);
        }
        jadwalKelas.setListPresensi(listPresensi);
    }

    @Override
    public void addJadwalKelas(JadwalKelasModel jadwalKelas) {
        setListPresensi(jadwalKelas);
        jadwalKelasDb.save(jadwalKelas);
    }

    @Override
    public void updateJadwalKelas(JadwalKelasModel jadwalKelas) {
        setListPresensi(jadwalKelas);
        jadwalKelasDb.save(jadwalKelas);
    }

    @Override
    public void deleteJadwalKelas(JadwalKelasModel jadwalKelas) {
        jadwalKelasDb.delete(jadwalKelas);
    }
}
