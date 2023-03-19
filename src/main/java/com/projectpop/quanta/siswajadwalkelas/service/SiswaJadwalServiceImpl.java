package com.projectpop.quanta.siswajadwalkelas.service;

import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswajadwalkelas.model.SiswaJadwalModel;
import com.projectpop.quanta.siswajadwalkelas.repository.SiswaJadwalDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SiswaJadwalServiceImpl implements SiswaJadwalService{
    @Autowired
    SiswaJadwalDb siswaJadwalDb;

    @Override
    public List<SiswaJadwalModel> getListSiswaJadwalBySiswa(SiswaModel siswa) {
        return siswaJadwalDb.findAllBySiswa(siswa);
    }

    @Override
    public List<SiswaJadwalModel> getListSiswaJadwalBySiswaAndDate(SiswaModel siswa, LocalDate tanggal) {
//        return siswaJadwalDb.findAllBySiswaAndJadwalKelas_StartDateClass_Date(siswa, tanggal);
        return null;
    }
}
