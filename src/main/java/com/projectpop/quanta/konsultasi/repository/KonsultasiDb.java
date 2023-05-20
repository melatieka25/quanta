package com.projectpop.quanta.konsultasi.repository;

import com.projectpop.quanta.jadwalkelas.model.JadwalKelasModel;
import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KonsultasiDb extends JpaRepository<KonsultasiModel, Integer> {

    List<KonsultasiModel> findAllByPengajarKonsul(PengajarModel pengajar);

    KonsultasiModel getById(Integer id);

    @Query("SELECT k FROM KonsultasiModel k WHERE k.tahunAjarKonsul =:tahunAjar AND MONTH(k.startTime) = :month ")
    Optional<List<KonsultasiModel>> findByTahunAjarKonsulAndMonth(@Param("tahunAjar") TahunAjarModel tahunAjar, @Param("month") Integer month);
}
