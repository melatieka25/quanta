package com.projectpop.quanta.statistics.service;

import com.projectpop.quanta.kelas.model.KelasModel;
import com.projectpop.quanta.statistics.model.StatsModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;

import java.util.List;
import java.util.Map;

public interface KonsulStatsService {
    StatsModel[] calculateValue(List<String> LabelName, TahunAjarModel tahunAjar, Integer month, String jenjang);
    StatsModel[] calculateValuePengajar(List<String> LabelName, TahunAjarModel tahunAjar, Integer month, String jenjang);
    Map<String, String> getOverview(StatsModel[] data, TahunAjarModel tahunAjar, Integer month, String jenjang);
}
