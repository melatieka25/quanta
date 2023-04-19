package com.projectpop.quanta.tahunajar.service;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import com.projectpop.quanta.tahunajar.repository.TahunAjarDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TahunAjarServiceImpl implements TahunAjarService{

    @Autowired
    private TahunAjarDb tahunAjarDb;

    @Override
    public TahunAjarModel getTahunAjarById(String id) {
        return tahunAjarDb.findById(Integer.valueOf(id)).orElse(null);
    }

    @Override
    public TahunAjarModel getTahunAjarAktif() {
        Optional<TahunAjarModel> tahunAjar = tahunAjarDb.findByIsAktifIsTrue();
        return tahunAjar.orElse(null);
    }


    @Override
    public List<TahunAjarModel> getAllTahunAjar() {
        return tahunAjarDb.findAll();
    }
}
