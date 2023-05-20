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
    public List<String> getAllMapelName() {
        return mataPelajaranDb.findAllName().orElse(null);
    }

    @Override
    public List<String> getMapelSMPName() {
        return mataPelajaranDb.findAllNameByIsSMPIsTrue().orElse(null);
    }

    @Override
    public List<String> getMapelSMAName() {
        return mataPelajaranDb.findAllNameByIsSMAIsTrue().orElse(null);
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
    public MataPelajaranModel getMapelById(Integer id) {
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

    @Override
    public String getJenjangMapel(MataPelajaranModel mapel){
        String jenjangMapel= "";
        if (null != mapel.getIsSMA() && null != mapel.getIsSMP()){
            jenjangMapel = "smpsma";
        } else if (null != mapel.getIsSMP()){
            jenjangMapel = "smp";
        } else {
            jenjangMapel = "sma";
        }
        return jenjangMapel;
    }

    @Override
    public void setJenjangMapel(MataPelajaranModel mapel, String jenjangMapel){
        if (jenjangMapel.equals("smp")){
            mapel.setIsSMP(true);
        } else  if (jenjangMapel.equals("sma")){
            mapel.setIsSMA(true);
        } else if (jenjangMapel.equals("smpsma")){
            mapel.setIsSMP(true);
            mapel.setIsSMA(true);
        }
    }

}
