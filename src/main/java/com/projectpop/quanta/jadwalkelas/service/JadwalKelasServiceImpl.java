package com.projectpop.quanta.jadwalkelas.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.repository.JadwalKelasDb;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.repository.PengajarDb;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.presensi.service.PresensiService;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;

import java.util.Optional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Autowired
    PengajarDb pengajarDb;

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

    @Override
    public List<JadwalKelasModel> getJadwalByPengajarAndTanggal(PengajarModel pengajar, LocalDate tanggal) {
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasDb.findAllByPengajarKelas(pengajar);
        List<JadwalKelasModel> listJadwalKelasNew = new ArrayList<JadwalKelasModel>();
        for (JadwalKelasModel jadwalKelas: listJadwalKelas) {
            if (jadwalKelas.getStartDateClass().toLocalDate().equals(tanggal)){
                listJadwalKelasNew.add(jadwalKelas);
            }
        }
        return listJadwalKelasNew;
    }
    
    @Override
    public List<JadwalKelasModel> getListJadwalKelasByIdPengajar(Integer idPengajar) {
        Optional<PengajarModel> pengajarKelas = pengajarDb.findById(idPengajar);
        if (pengajarKelas.isPresent()){
            return jadwalKelasDb.findAllByPengajarKelas(pengajarKelas.get());
        }
        return new ArrayList<>();
    }
}
