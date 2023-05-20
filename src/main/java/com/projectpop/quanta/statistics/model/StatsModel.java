package com.projectpop.quanta.statistics.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatsModel implements Comparable<StatsModel>{
    String name;
    float value;

    public StatsModel(String name, float value){
        this.name = name;
        this.value = value;
    }

    @Override
    public int compareTo(StatsModel itemStats){
        return compare(this.value, itemStats.value);
    }

    private int compare(Float a, Float b){
       return a.compareTo(b);
    }
}
