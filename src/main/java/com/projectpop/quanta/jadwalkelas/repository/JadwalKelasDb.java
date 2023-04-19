package com.projectpop.quanta.jadwalkelas.repository;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


@Repository
public interface JadwalKelasDb extends JpaRepository<JadwalKelasModel, String>{
    JadwalKelasModel findById(Integer id);
    List<JadwalKelasModel> findAllByPengajarKelas(PengajarModel pengajar);
    List<JadwalKelasModel> findAllByKelas(KelasModel kelas);
    @Query("SELECT j FROM JadwalKelasModel j WHERE j.kelas.tahunAjar = :tahunAjar AND MONTH(j.startDateClass) = :month")
    List<JadwalKelasModel> findAllByTahunAjarAndMonth(@Param("tahunAjar") TahunAjarModel tahunAjar, @Param("month") Integer month);
}
