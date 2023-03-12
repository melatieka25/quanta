package com.projectpop.quanta.pengajar.service;

import java.util.List;

import com.projectpop.quanta.pengajar.model.PengajarModel;

public interface PengajarService {

    void addPengajar(PengajarModel pengajar);
    List<PengajarModel> getListPengajar();
    String getPengajarMapel(PengajarModel pengajar);

}
