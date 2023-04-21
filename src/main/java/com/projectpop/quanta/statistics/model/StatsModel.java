package com.projectpop.quanta.statistics.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatsModel implements Comparable<StatsModel>{
    String name;
    float percentage;

    public StatsModel(String name, float percentage){
        this.name = name;
        this.percentage = percentage;
    }

    @Override
    public int compareTo(StatsModel itemStats){
        return compare(this.percentage, itemStats.percentage);
    }

    private int compare(Float a, Float b){
       return a.compareTo(b);
    }
}
