package com.example.searcher.domain.dto;


import java.util.List;

public class Tuple {
    public int fileno;
    public List<Integer> position;

    public Tuple(int fileno,List<Integer> position) {
        this.fileno = fileno;
        this.position = position;
    }
}