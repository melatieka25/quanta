package com.projectpop.quanta.statistics.service;

import com.projectpop.quanta.konsultasi.model.KonsultasiModel;
import com.projectpop.quanta.konsultasi.service.KonsultasiService;
import com.projectpop.quanta.statistics.model.StatsModel;
import com.projectpop.quanta.tahunajar.model.TahunAjarModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class KonsulStatsServiceImpl implements KonsulStatsService{

    @Qualifier("konsultasiServiceImpl")
    @Autowired
    private KonsultasiService konsultasiService;


    @Override
    public StatsModel[] calculateValuePengajar(List<String> listLabelPengajar, TahunAjarModel tahunAjar, Integer month, String jenjang) {
        StatsModel dataStats[] = new StatsModel[listLabelPengajar.size()];

        // Initialize Data
        int counter = 0;
        for (String label : listLabelPengajar){
            dataStats[counter++] = new StatsModel(label, 0f);
        }

        List<KonsultasiModel> listKonsultasi = konsultasiService.getListKonsultasiByTahunAjarAndMonth(tahunAjar, month);

        for (KonsultasiModel konsultasi : listKonsultasi) {
            if (konsultasi.getStatus().getDisplayValue().equals("Ditutup") && konsultasi.getJenjangKelas().getDisplayValue().contains(jenjang)) {
                for (StatsModel data : dataStats) {
                    if (konsultasi.getPengajarKonsul().getName().equals(data.getName())) {
                        data.setValue(data.getValue() + konsultasi.getDuration());
                    }
                }
            }
        }

        return dataStats;
    }

    @Override
    public StatsModel[] calculateValue(List<String> listLabel, TahunAjarModel tahunAjar, Integer month, String jenjang) {
        StatsModel dataStats[] = new StatsModel[listLabel.size()];

        // Initialize Data
        int counter = 0;
        for (String label : listLabel){
            dataStats[counter++] = new StatsModel(label, 0f);
        }

        List<KonsultasiModel> listKonsultasi = konsultasiService.getListKonsultasiByTahunAjarAndMonth(tahunAjar, month);

        for (KonsultasiModel konsultasi : listKonsultasi) {
            if (konsultasi.getStatus().getDisplayValue().equals("Ditutup") && konsultasi.getJenjangKelas().getDisplayValue().contains(jenjang)) {
                for (StatsModel data : dataStats) {
                    if (konsultasi.getMapelKonsul().getName().equals(data.getName())) {
                        data.setValue(data.getValue() + konsultasi.getDuration());
                    }
                }
            }
        }

        return dataStats;
    }

    @Override
    public Map<String, String> getOverview(StatsModel[] data, TahunAjarModel tahunAjar, Integer month, String jenjang) {
        Map<String, String> mapOverview = new HashMap<>();
        StatsModel[] dataStatsCopy = new StatsModel[data.length];

        System.arraycopy(data, 0, dataStatsCopy, 0, data.length);

        Arrays.sort(dataStatsCopy);
        
        String lowestData;
        String highestData;
        if (dataStatsCopy.length == 0){
            lowestData = "-";
            highestData = "-";

        } else {
            lowestData = dataStatsCopy[0].getName();
            highestData = dataStatsCopy[dataStatsCopy.length-1].getName();
        }
       

        mapOverview.put("Konsultasi " + jenjang + " Tertinggi", highestData);
        mapOverview.put("Konsultasi " + jenjang + " Terendah", lowestData);

        return mapOverview;
    }
}
