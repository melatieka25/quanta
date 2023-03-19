package com.projectpop.quanta.pengajarmapel.repository;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PengajarMapelDb extends JpaRepository<PengajarMapelModel, String> {
    List<PengajarMapelModel> deleteAllByMapel(MataPelajaranModel mapel);
}
