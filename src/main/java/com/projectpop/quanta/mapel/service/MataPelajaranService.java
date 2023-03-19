package com.projectpop.quanta.mapel.service;

import java.util.List;
import com.projectpop.quanta.mapel.model.MataPelajaranModel;

public interface MataPelajaranService {
    List<MataPelajaranModel> getListMapel();
    MataPelajaranModel getMapelById(Integer id);
}
