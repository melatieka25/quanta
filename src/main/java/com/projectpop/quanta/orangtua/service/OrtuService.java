package com.projectpop.quanta.orangtua.service;

import java.util.List;

import com.projectpop.quanta.orangtua.model.OrtuModel;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.model.SiswaCsvModel;

public interface OrtuService {
    List<OrtuModel> getListOrtu();
    void addOrtu(OrtuModel ortu);
    OrtuModel getOrtuById(int id);
    String getAnakAktif(OrtuModel ortu);
    OrtuModel inactiveOrtu(OrtuModel ortu);
    OrtuModel activeOrtu(OrtuModel ortu);
    OrtuModel updateOrtu(OrtuModel ortu);
    OrtuModel getOrtuByEmail(String email);
    SiswaModel getDefaultAnakTerpilih(OrtuModel ortu);
    OrtuModel convertOrtuCsv(SiswaCsvModel siswaCsv);
}
