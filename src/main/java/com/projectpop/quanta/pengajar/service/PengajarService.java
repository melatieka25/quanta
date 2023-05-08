package com.projectpop.quanta.pengajar.service;
import java.util.List;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.pengajar.model.PengajarCsvModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.user.model.UserModel;
import org.springframework.ui.Model;

public interface PengajarService {
    List<PengajarModel> getListPengajarActive();
    void addPengajar(PengajarModel pengajar);
    List<PengajarModel> getListPengajar();
    String getPengajarMapel(PengajarModel pengajar);
    String getKelasAsuh(PengajarModel pengajar);
    PengajarModel getPengajarById(Integer id);
    PengajarModel inactivePengajar(PengajarModel pengajar);
    PengajarModel activePengajar(PengajarModel pengajar);
    PengajarModel updatePengajar(PengajarModel pengajar);
    int getNumberOfKelasAktif(PengajarModel pengajar);
    int getNumberOfKonsultasiAktif(PengajarModel pengajar);
    PengajarModel getPengajarByEmail(String email);
    List<PengajarModel> getListKakakAsuh();
    void checkIsPengajarDanKakakAsuh(UserModel userModel, Model model);
    List<KelasModel> getListKelasAsuh(PengajarModel pengajar);
    PengajarModel convertPengajarCsv(PengajarCsvModel pengajarCsv);


}
