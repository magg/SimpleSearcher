package com.example.searcher.domain.dto;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(JUnit4.class)
public class RankingTests {


    @Test
    public void sortingTest(){
        Ranking r1 = new Ranking(4.0, 2);
        Ranking r2 = new Ranking(10.0, 9);

        List<Ranking> list = new ArrayList<>();

        list.add(r1);
        list.add(r2);

        Collections.sort(list, new Ranking());


        assertEquals(10.0,list.get(0).rank);


    }

}

