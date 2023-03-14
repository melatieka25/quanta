package com.projectpop.quanta.pengajarmapel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajarmapel.model.PengajarMapelModel;
import com.projectpop.quanta.pengajarmapel.repository.PengajarMapelDb;

@Service
@Transactional
public class PengajarMapelServiceImpl implements PengajarMapelService{
    @Autowired
    PengajarMapelDb pengajarMapelDb;

    @Override
    public List<PengajarMapelModel> getListPengajarByMapel(MataPelajaranModel mapel) {
       return pengajarMapelDb.findByMapel(mapel);
    }

    @Override
    public List<PengajarMapelModel> getListMapelByPengajar(PengajarModel pengajar) {
        return pengajarMapelDb.findByPengajar(pengajar);
    }
}
