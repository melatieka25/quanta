package com.projectpop.quanta.tahunajar.service;

import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import com.projectpop.quanta.tahunajar.repository.TahunAjarDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TahunAjarServiceImpl implements TahunAjarService{
    @Autowired
    TahunAjarDb tahunAjarDb;

    @Override
    public List<TahunAjarModel> getAllTahunAjar() {
        return tahunAjarDb.findAll();
    }
}
