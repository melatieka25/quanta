package com.projectpop.quanta.pesan.service;

import com.projectpop.quanta.pesan.model.PesanModel;
import com.projectpop.quanta.siswa.model.SiswaModel;

import java.util.List;

public interface PesanService {
    List<PesanModel> getPesanBySiswa(SiswaModel siswaModel);
    PesanModel createPesanModel(PesanModel pesanModel);
}
