package com.projectpop.quanta.pesan.service;

import com.projectpop.quanta.pesan.model.PesanModel;
import com.projectpop.quanta.pesan.repository.PesanDb;
import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.service.SiswaService;
import com.projectpop.quanta.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PesanServiceImpl implements PesanService{
    @Autowired
    SiswaService siswaService;

    @Autowired
    UserService userService;

    @Autowired
    PesanDb pesanDb;

    @Override
    public List<PesanModel> getPesanBySiswa(SiswaModel siswaModel) {
       Optional<List<PesanModel>> listPesanModel = pesanDb.findAllBySiswaPesan(siswaModel);
       if (listPesanModel.isPresent()){
           return listPesanModel.get();
       }
       else {
           return null;
       }
    }
}
