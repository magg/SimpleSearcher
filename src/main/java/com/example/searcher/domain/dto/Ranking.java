
package com.example.searcher.domain.dto;


import java.util.Comparator;

public class Ranking  implements Comparator<Ranking> {
    public Double rank;
    public int doc;

    public Ranking(){}

    public Ranking(Double rank, int doc){
        this.rank = rank;
        this.doc = doc;
    }

    public int compare (Ranking i1, Ranking i2){

        return Double.compare(i2.rank, i1.rank);

    }

}