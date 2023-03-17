package com.projectpop.quanta.siswa.service;

import com.projectpop.quanta.siswa.model.SiswaModel;
import com.projectpop.quanta.siswa.repository.SiswaDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SiswaServiceImpl implements SiswaService {
    @Autowired
    private SiswaDb siswaDb;

    @Override
    public List<SiswaModel> getListSiswa() {
        return siswaDb.findAll();
    }

}
