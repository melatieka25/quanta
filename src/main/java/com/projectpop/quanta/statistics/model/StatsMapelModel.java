package com.projectpop.quanta.statistics.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatsMapelModel implements Comparable<StatsMapelModel>{
    String mapelName;
    float percentage;

    public StatsMapelModel(String mapelName, float percentage){
        this.mapelName = mapelName;
        this.percentage = percentage;
    }

    @Override
    public int compareTo(StatsMapelModel mapel){
        return compare(this.percentage, mapel.percentage);
    }

    private int compare(Float a, Float b){
       return a.compareTo(b);
    }
}
