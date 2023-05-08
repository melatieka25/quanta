package com.projectpop.quanta.mapel.service;


import com.projectpop.quanta.mapel.model.MataPelajaranModel;

import java.util.List;

public interface MapelService {
    List<MataPelajaranModel> getAllMapel();
    List<MataPelajaranModel> getMapelSMP();
    List<MataPelajaranModel> getMapelSMA();
    MataPelajaranModel getMapelById(Integer id);
    MataPelajaranModel addMapel(MataPelajaranModel mapel);
    void deleteMapel(MataPelajaranModel mapel);
    String getJenjangMapel(MataPelajaranModel mapel);
    void setJenjangMapel(MataPelajaranModel mapel, String jenjangMapel);

}
