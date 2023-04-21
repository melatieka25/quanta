package com.projectpop.quanta.mapel.service;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.repository.MataPelajaranDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class MapelServiceImpl implements MapelService{
    @Autowired
    private MataPelajaranDb mataPelajaranDb;

    @Override
    public List<MataPelajaranModel> getAllMapel() {
        return mataPelajaranDb.findAll() ;
    }

    @Override
    public List<MataPelajaranModel> getMapelSMP() {
        return mataPelajaranDb.findByIsSMPIsTrue().orElse(null);
    }

    @Override
    public List<MataPelajaranModel> getMapelSMA() {
        return mataPelajaranDb.findByIsSMAIsTrue().orElse(null);
    }

    @Override
    public MataPelajaranModel getMapelbyId(Integer id) {
        return mataPelajaranDb.findById(id).orElse(null);
    }

    @Override
    public MataPelajaranModel addMapel(MataPelajaranModel mapel) {
        return mataPelajaranDb.save(mapel);
    }

    @Override
    public void deleteMapel(MataPelajaranModel mapel) {
        mataPelajaranDb.delete(mapel);
    }

}
