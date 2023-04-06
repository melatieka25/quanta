package com.projectpop.quanta.mapel.service;


import com.projectpop.quanta.mapel.model.MataPelajaranModel;

import java.util.List;

public interface MapelService {
    List<MataPelajaranModel> getAllMapel();
    MataPelajaranModel getMapelbyId(Integer id);
    MataPelajaranModel addMapel(MataPelajaranModel mapel);
    void deleteMapel(MataPelajaranModel mapel);

}
