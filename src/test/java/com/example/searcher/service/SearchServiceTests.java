package com.example.searcher.service;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)

public class SearchServiceTests {


    SearchService ss = new SearchService();

    @Before
    public void setUp(){

        try {
            ss.index.getStopWords();

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("inputFiles/data2.txt").getFile());
            File file2 = new File(classLoader.getResource("inputFiles/folder/data3.txt").getFile());
            File file1 = new File(classLoader.getResource("inputFiles/data1.txt").getFile());
            ss.index.fileList.add(file1);
            ss.index.fileList.add(file);
            ss.index.fileList.add(file2);

            ss.index.fileReader(file1,0);
            ss.index.fileReader(file,1);
            ss.index.fileReader(file2,2);

            ss.index.buildTF();

        } catch (IOException e) {

            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    @Test
    public void testOneWordSearch(){


        List<String> words = ss.index.getTerms("agreeable");

        List <Integer > res = ss.oneWordsearch(words, "agreeable");


        assertEquals(2,res.size());

    }


    @Test
    public void testFreeTextSearch(){


        List<String> words = ss.index.getTerms("agreeable arya");

        List <Integer > res = ss.freeTextSearch(words, "agreeable");


        assertEquals(2,res.size());

    }



    @Test
    public void testFullTextSearch(){


        List<String> words = ss.index.getTerms("agreeable preferred");

        List <Integer > res = ss.fullTextSearch(words, "agreeable preferred");


        assertEquals(1,res.size());

    }


    @Test
    public void testIntersection(){

        List<Integer> l1 = Arrays.asList(1,2,3);
        List<Integer>  l2 = Arrays.asList(2,4,6);
        List<Integer>  l3 = Arrays.asList(2,5,9);

        List<List<Integer>> problem = Arrays.asList(l1,l2,l3);

       List<Integer> res =  ss.computeIntersection(problem);
       int value = 2;
       int valor = res.get(0);
       assertEquals(value, valor);

    }

}
