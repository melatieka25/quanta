package com.projectpop.quanta.pengajar.service;

import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.repository.PengajarDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PengajarServiceImpl implements PengajarService{
    @Autowired
    PengajarDb pengajarDb;

    @Override
    public List<PengajarModel> getListKakakAsuh() {
        Optional<List<PengajarModel>> kakakAsuhList = pengajarDb.findKakakAsuh();
        return kakakAsuhList.orElse(null);
    }
}
