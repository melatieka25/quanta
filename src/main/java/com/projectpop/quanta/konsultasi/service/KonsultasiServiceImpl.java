package com.projectpop.quanta.konsultasi.service;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.model.StatusKonsul;
import com.projectpop.quanta.konsultasi.repository.KonsultasiDb;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.Jenjang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.projectpop.quanta.konsultasi.model.StatusKonsul.*;

@Service
@Transactional
public class KonsultasiServiceImpl implements KonsultasiService{
    @Autowired
    KonsultasiDb konsultasiDb;

    @Override
    public List<KonsultasiModel> getListKonsultasiHariIni() {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        List<KonsultasiModel> listKonsultasiToday = new ArrayList<>();

        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStartTime().toLocalDate().equals(LocalDate.now())){
                listKonsultasiToday.add(konsultasi);
            }
        }
        return listKonsultasiToday;
    }

    @Override
    public KonsultasiModel getKonsultasi(Integer id) {
        Optional<KonsultasiModel> konsultasi = konsultasiDb.findById(id);
        if (konsultasi.isPresent()) {
            return konsultasi.get();
        } return null;
    }

    @Override
    public List<KonsultasiModel> getListMyKonsultasiPengajar(PengajarModel pengajar) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAllByPengajarKonsul(pengajar);
        List<KonsultasiModel> listKonsultasiPengajar = new ArrayList<KonsultasiModel>();

        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStatus().equals(DITERIMA)) {
                listKonsultasiPengajar.add(konsultasi);
            }
        }
        return listKonsultasiPengajar;

    }

    @Override
    public List<KonsultasiModel> getListKonsultasiByJenjangAndStatus(Jenjang jenjang, StatusKonsul status) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        List<KonsultasiModel> listRequestKonsultasiJenjangStatus = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getJenjangKelas().equals(jenjang) && konsultasi.getStatus().equals(status)) {
                listRequestKonsultasiJenjangStatus.add(konsultasi);
            }
        }

        return listRequestKonsultasiJenjangStatus;
    }

    @Override
    public List<KonsultasiModel> getListMyKonsultasiPengajarAndStatus(PengajarModel pengajar, StatusKonsul status) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAllByPengajarKonsul(pengajar);
        List<KonsultasiModel> listKonsultasiStatus = new ArrayList<KonsultasiModel>();

        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStatus().equals(status)) {
                listKonsultasiStatus.add(konsultasi);
            }
        }
        return listKonsultasiStatus;
    }

    @Override
    public KonsultasiModel createKonsultasi(KonsultasiModel konsultasiModel) {
        return konsultasiDb.save(konsultasiModel);
    }

    @Override
    public List<KonsultasiModel> getListKonsultasiByPengajarAndTanggal(PengajarModel pengajar, LocalDate tanggal) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAllByPengajarKonsul(pengajar);
        List<KonsultasiModel> listKonsultasiNew = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStartTime().toLocalDate().equals(tanggal)){
                listKonsultasiNew.add(konsultasi);
            }
        }
        return listKonsultasiNew;
    }

    @Override
    public void reloadStatus() {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStatus().equals(PENDING) && konsultasi.getStartTime().minusHours(1).isBefore(LocalDateTime.now())) {
                konsultasi.setStatus(KADALUARSA);
                konsultasiDb.save(konsultasi);
            }
        }

    }

    @Override
    public List<KonsultasiModel> getListKonsultasiByJenjangHariIni(Jenjang jenjang) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        List<KonsultasiModel> listRequestKonsultasiJenjangHariIni = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getJenjangKelas().equals(jenjang) && konsultasi.getStartTime().toLocalDate().equals(LocalDate.now())) {
                listRequestKonsultasiJenjangHariIni.add(konsultasi);
            }
        }

        return listRequestKonsultasiJenjangHariIni;
    }

    @Override
    public List<KonsultasiModel> getListKonsultasiStatus(StatusKonsul status) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        List<KonsultasiModel> listRequestKonsultasiStatus = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStatus().equals(status)) {
                listRequestKonsultasiStatus.add(konsultasi);
            }
        }

        return listRequestKonsultasiStatus;
    }

    @Override
    public List<KonsultasiModel> getListKonsultasiByPengajarAndStatusAndTanggal(PengajarModel pengajar, StatusKonsul satus, LocalDate tanggal) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAllByPengajarKonsul(pengajar);
        List<KonsultasiModel> listKonsultasiNew = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStartTime().toLocalDate().equals(tanggal) && konsultasi.getStatus().equals(satus)){
                listKonsultasiNew.add(konsultasi);
            }
        }
        return listKonsultasiNew;
    }
}
