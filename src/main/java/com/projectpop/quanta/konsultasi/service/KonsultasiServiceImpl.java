package com.projectpop.quanta.konsultasi.service;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.repository.KonsultasiDb;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.siswa.model.Jenjang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.projectpop.quanta.konsultasi.model.StatusKonsul.*;
//import static com.projectpop.quanta.konsultasi.model.StatusKonsul.DITERIMA;
//import static com.projectpop.quanta.konsultasi.model.StatusKonsul.PENDING;

@Service
@Transactional
public class KonsultasiServiceImpl implements KonsultasiService{
    @Autowired
    KonsultasiDb konsultasiDb;

    @Override
    public List<KonsultasiModel> getListKonsultasi() {
        return konsultasiDb.findAll();
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
    public List<KonsultasiModel> getListKonsultasiByJenjang(Jenjang jenjang) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAll();
        List<KonsultasiModel> listRequestKonsultasiJenjang = new ArrayList<KonsultasiModel>();
        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getJenjangKelas().equals(jenjang)) {
                listRequestKonsultasiJenjang.add(konsultasi);
            }
        }

        return listRequestKonsultasiJenjang;
    }

    @Override
    public List<KonsultasiModel> getListMyRequestKonsultasi(PengajarModel pengajar) {
        List<KonsultasiModel> listKonsultasi = konsultasiDb.findAllByPengajarKonsul(pengajar);
        List<KonsultasiModel> listRequestKonsultasiPengajar = new ArrayList<KonsultasiModel>();

        for (KonsultasiModel konsultasi: listKonsultasi) {
            if (konsultasi.getStatus().equals(PENDING)) {
                listRequestKonsultasiPengajar.add(konsultasi);
            }
        }
        return listRequestKonsultasiPengajar;
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
        if (listKonsultasiNew.size()!=0) {
            return listKonsultasiNew;
        } return null;
    }
}
