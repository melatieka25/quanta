package com.projectpop.quanta.orangtua.service;

import java.util.List;

import com.projectpop.quanta.orangtua.model.OrtuModel;

public interface OrtuService {
    List<OrtuModel> getListOrtu();
    void addOrtu(OrtuModel ortu);
    OrtuModel getOrtuById(int id);
    String getAnakAktif(OrtuModel ortu);
    OrtuModel inactiveOrtu(OrtuModel ortu);
    OrtuModel activeOrtu(OrtuModel ortu);
    OrtuModel updateOrtu(OrtuModel ortu);
}
