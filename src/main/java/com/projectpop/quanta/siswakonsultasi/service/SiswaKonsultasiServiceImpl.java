package com.projectpop.quanta.siswakonsultasi.service;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.model.StatusKonsul;
import com.projectpop.quanta.konsultasi.repository.KonsultasiDb;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.repository.SiswaDb;
import com.projectpop.quanta.siswakonsultasi.model.SiswaKonsultasiModel;
import com.projectpop.quanta.siswakonsultasi.repository.SiswaKonsultasiDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.projectpop.quanta.konsultasi.model.StatusKonsul.*;


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

    @Override
    public List<SiswaKonsultasiModel> getListKonsultasiBySiswaAndStatus(SiswaModel siswa, StatusKonsul status) {
        List<SiswaKonsultasiModel> listSiswaKonsultasi =  siswaKonsultasiDb.findAllBySiswaKonsul(siswa);
        List<SiswaKonsultasiModel> listSiswaKonsultasiStatus = new ArrayList<>();

        for (SiswaKonsultasiModel siswaKonsultasi: listSiswaKonsultasi) {
            if (siswaKonsultasi.getKonsultasi().getStatus().equals(status)){
                listSiswaKonsultasiStatus.add(siswaKonsultasi);
            }
        } return listSiswaKonsultasiStatus;


    }

    @Override
    public List<SiswaKonsultasiModel> getListKonsultasiBySiswaAndTanggal(SiswaModel siswa, LocalDate tanggal) {
        List<SiswaKonsultasiModel> listSiswaKonsultasi =  siswaKonsultasiDb.findAllBySiswaKonsul(siswa);
        List<SiswaKonsultasiModel> listSiswaKonsultasiTanggal = new ArrayList<>();

        for (SiswaKonsultasiModel siswaKonsultasi: listSiswaKonsultasi) {
            if (siswaKonsultasi.getKonsultasi().getStartTime().toLocalDate().equals(tanggal)){
                listSiswaKonsultasiTanggal.add(siswaKonsultasi);
            }
        } return listSiswaKonsultasiTanggal;
    }

    @Override
    public List<SiswaKonsultasiModel> getListKonsultasiBySiswaAndTanggalPendingAndDiterima(SiswaModel siswa, LocalDate tanggal) {
        List<SiswaKonsultasiModel> listSiswaKonsultasi =  siswaKonsultasiDb.findAllBySiswaKonsul(siswa);
        List<SiswaKonsultasiModel> listSiswaKonsultasiTanggal = new ArrayList<>();

        for (SiswaKonsultasiModel siswaKonsultasi: listSiswaKonsultasi) {
            KonsultasiModel konsultasi = siswaKonsultasi.getKonsultasi();
            if (konsultasi.getStartTime().toLocalDate().equals(tanggal)
                    && (konsultasi.getStatus().equals(PENDING)
                    || konsultasi.getStatus().equals(DITERIMA)) ){
                listSiswaKonsultasiTanggal.add(siswaKonsultasi);
            }
        } return listSiswaKonsultasiTanggal;
    }

    @Override
    public boolean isRekomended(SiswaModel siswa, KonsultasiModel konsultasi) {
        List<SiswaKonsultasiModel> listKonsultasi = konsultasi.getListSiswaKonsultasi();
        for (SiswaKonsultasiModel siswaKonsultasi: listKonsultasi) {
            if (siswaKonsultasi.getSiswaKonsul().equals(siswa)){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<SiswaKonsultasiModel> getListKonsultasiSiswaHariIni(SiswaModel siswa) {
        List<SiswaKonsultasiModel> listSiswaKonsultasi =  siswaKonsultasiDb.findAllBySiswaKonsul(siswa);
        List<SiswaKonsultasiModel> listKonsultasiSiswaHariIni = new ArrayList<>();

        for (SiswaKonsultasiModel siswaKonsultasi: listSiswaKonsultasi) {
            KonsultasiModel konsultasi = siswaKonsultasi.getKonsultasi();
            if (konsultasi.getStartTime().toLocalDate().equals(LocalDate.now())
                    && konsultasi.getStatus().equals(DITERIMA) ){
                listKonsultasiSiswaHariIni.add(siswaKonsultasi);
            }
        } return listKonsultasiSiswaHariIni;
    }

    @Override
    public SiswaKonsultasiModel updateSiswaKonsultasi(SiswaKonsultasiModel siswaKonsultasi) {
        siswaKonsultasiDb.save(siswaKonsultasi);
        return siswaKonsultasi;
    }
}
