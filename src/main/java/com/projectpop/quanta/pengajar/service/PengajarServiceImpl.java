package com.projectpop.quanta.pengajar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpop.quanta.pengajar.model.PengajarModel;
import com.projectpop.quanta.pengajar.repository.PengajarDb;
import java.util.List;

@Service
@Transactional
public class PengajarServiceImpl implements PengajarService {
    @Autowired
    PengajarDb pengajarDb;

    @Override
    public PengajarModel getPengajarById(Integer id) {
        return pengajarDb.findById(id);
    }
    
    @Override
    public List<PengajarModel> getListPengajar() {
        return pengajarDb.findAll();
    }
}
