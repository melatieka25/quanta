package com.projectpop.quanta.kelas.repository;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.siswa.model.Jenjang;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectpop.quanta.kelas.model.JadwalAvail;
import com.projectpop.quanta.kelas.model.KelasModel;
import java.util.List;
import java.util.Optional;

@Repository
public interface KelasDb extends JpaRepository<KelasModel, String> {
    List<KelasModel> findByDays(JadwalAvail days);
    Optional<KelasModel> findById(Integer id);
    List<KelasModel> findByName(String name);
    List<KelasModel> findByTahunAjar(TahunAjarModel tahunAjar);
    Optional<List<KelasModel>> findByIsSMAIsTrue();
    Optional<List<KelasModel>> findByIsSMPIsTrue();
}
