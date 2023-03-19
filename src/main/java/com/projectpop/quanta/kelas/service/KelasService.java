package com.projectpop.quanta.kelas.service;

import com.projectpop.quanta.kelas.model.KelasModel;
import java.util.List;

public interface KelasService {
    List<KelasModel> getListKelas();
    List<KelasModel> getListKelasByDays(Integer day);
    KelasModel getKelasById(Integer id);
    void addKelas(KelasModel kelas);
    void deleteKelas(KelasModel kelas);

}
