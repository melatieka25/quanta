package com.projectpop.quanta.tahunajar.service;

import com.projectpop.quanta.tahunajar.model.TahunAjarModel;

import java.util.List;
import java.util.Optional;

public interface TahunAjarService {
    List<TahunAjarModel> getAllTahunAjar();
    TahunAjarModel getTahunAjarById(String id);
    TahunAjarModel getTahunAjarAktif();
}
