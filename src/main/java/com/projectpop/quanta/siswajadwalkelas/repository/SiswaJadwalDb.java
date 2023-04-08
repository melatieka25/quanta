package com.projectpop.quanta.siswajadwalkelas.repository;

import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswajadwalkelas.model.SiswaJadwalModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SiswaJadwalDb extends JpaRepository<SiswaJadwalModel, Integer> {
    List<SiswaJadwalModel> findAllBySiswa(SiswaModel siswa);

//    List<SiswaJadwalModel> findAllBySiswaAndJadwalKelas_StartDateClass_Date(SiswaModel siswa, LocalDate tanggal);

}
