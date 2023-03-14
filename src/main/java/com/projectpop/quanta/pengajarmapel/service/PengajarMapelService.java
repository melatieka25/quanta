package com.projectpop.quanta.pengajarmapel.service;

import java.util.List;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;

public interface PengajarMapelService {
    List<PengajarMapelModel> getListPengajarByMapel(MataPelajaranModel mapel);
    List<PengajarMapelModel> getListMapelByPengajar(PengajarModel pengajar);
}
