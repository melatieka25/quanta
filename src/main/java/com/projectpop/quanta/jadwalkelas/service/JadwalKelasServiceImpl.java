package com.projectpop.quanta.jadwalkelas.service;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.jadwalkelas.repository.JadwalKelasDb;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.repository.PengajarDb;
import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.presensi.service.PresensiService;
import com.projectpop.quanta.siswakelas.model.SiswaKelasModel;

import java.util.Optional;

import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
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

    public void createListPresensi(JadwalKelasModel jadwalKelas) {
        List<PresensiModel> listPresensi = new ArrayList<>();
        
        // create presensi per student
        // System.out.println("==== " + jadwalKelas.getKelas().getName());
        List<SiswaKelasModel> listSiswa = jadwalKelas.getKelas().getListSiswaKelas();
        for (SiswaKelasModel siswa : listSiswa) {
            System.out.println(siswa.getKelasSiswa().getName());
            PresensiModel presensi = presensiService.createPresensi(jadwalKelas);
            presensi.setSiswa(siswa.getSiswa());
            PresensiModel newPresensi = presensiService.updatePresensi(presensi);
            listPresensi.add(newPresensi);
        }
        jadwalKelas.setListPresensi(listPresensi);
    }

    @Override
    public void addJadwalKelas(JadwalKelasModel jadwalKelas) {
        createListPresensi(jadwalKelas);
        jadwalKelasDb.save(jadwalKelas);
    }

    @Override
    public void updateJadwalKelas(JadwalKelasModel jadwalKelas) {
        // System.out.println(jadwalKelas.getListPresensi());
        presensiService.deletePresensi(jadwalKelas);
        List<PresensiModel> newPresensi = new ArrayList<>();
        jadwalKelas.setListPresensi(newPresensi);

        // createListPresensi(jadwalKelas);
        // jadwalKelasDb.save(jadwalKelas);
    }

    @Override
    public void deleteJadwalKelas(JadwalKelasModel jadwalKelas) {
        jadwalKelasDb.delete(jadwalKelas);
    }

    @Override
    public List<JadwalKelasModel> getListJadwalKelasByKelas(KelasModel kelas) {
        return jadwalKelasDb.findAllByKelas(kelas);
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

    @Override
    public List<JadwalKelasModel> getListJadwalKelasByTahunAjarAndMonth(TahunAjarModel tahunAjar, Integer month) {
        return jadwalKelasDb.findAllByTahunAjarAndMonth(tahunAjar, month);
    }

    @Override
    public List<JadwalKelasModel> getListJadwalKelasByKelasAndTanggal(LocalDate tanggal, KelasModel kelas) {
        List<JadwalKelasModel> listJadwalKelas = jadwalKelasDb.findAllByKelas(kelas);
        List<JadwalKelasModel> listJadwalKelasTanggal = new ArrayList<>();
        for (JadwalKelasModel jadwalKelas: listJadwalKelas) {
            if (jadwalKelas.getStartDateClass().toLocalDate().equals(tanggal)){
                listJadwalKelasTanggal.add(jadwalKelas);
            }
        } return listJadwalKelasTanggal;
    }
}
