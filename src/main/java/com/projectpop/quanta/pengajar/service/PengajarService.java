package com.projectpop.quanta.pengajar.service;

import com.projectpop.quanta.pengajar.model.PengajarModel;
import java.util.List;

public interface PengajarService {
    PengajarModel getPengajarById(Integer id);
    List<PengajarModel> getListPengajar();
}
