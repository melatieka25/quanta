package com.projectpop.quanta.presensi.service;

import com.projectpop.quanta.presensi.model.PresensiModel;
import com.projectpop.quanta.presensi.model.PresensiStatus;

import java.util.List;

public interface PresensiService {
    PresensiModel getPresensiModelById(Integer id);
//    List<PresensiStatus> listPresensiStatus();
    List<PresensiModel> getListPresensi();
//    void addPresensi(PresensiModel presensiModel);
    void updatePresensi(PresensiModel presensiModel);
}
