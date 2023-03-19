package com.projectpop.quanta.pengajarmapel.service;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajar.service.PengajarService;
import com.projectpop.quanta.pengajarmapel.repository.PengajarMapelDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PengajarMapelServiceImpl implements PengajarMapelService {
    @Autowired
    PengajarMapelDb pengajarMapelDb;

    @Override
    public void deleteAllByMapel(MataPelajaranModel mapel){
        pengajarMapelDb.deleteAllByMapel(mapel);
    };
}
