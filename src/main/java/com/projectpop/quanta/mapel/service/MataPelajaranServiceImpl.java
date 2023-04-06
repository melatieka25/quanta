package com.projectpop.quanta.mapel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import com.projectpop.quanta.mapel.model.MataPelajaranModel;
import com.projectpop.quanta.mapel.repository.MataPelajaranDb;
import java.util.Optional;

@Service
@Transactional
public class MataPelajaranServiceImpl implements MataPelajaranService{
    @Autowired
    MataPelajaranDb mataPelajaranDb;

    @Override
    public List<MataPelajaranModel> getListMapel() {
        return mataPelajaranDb.findAll();
    }

    @Override
    public MataPelajaranModel getMapelById(Integer id) {
        Optional<MataPelajaranModel> mapel = mataPelajaranDb.findById(id);
        return mapel.orElse(null);
    }
}
