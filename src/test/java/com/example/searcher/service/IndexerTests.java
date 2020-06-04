package com.example.searcher.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class IndexerTests {

    Indexer i = new Indexer();


    @Test
    public void testStopWordsSize(){
        try {
            i.getStopWords();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(127, i.stopWords.size());

    }

    @Test
    public void testStopWordsRemoval(){
        try {
            i.getStopWords();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> terms = i.getTerms("a lonely boy");

        assertFalse(terms.contains("a"));
        //assertEquals(2, terms.size());

    }

    @Test
    public void testTermsSize(){
        try {
            i.getStopWords();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> terms = i.getTerms("a lonely boy");

        assertEquals(2, terms.size());

    }

    @Test
    public void testInvertedIndexValues(){

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("inputFiles/data2.txt").getFile());
        File file2 = new File(classLoader.getResource("inputFiles/folder/data3.txt").getFile());

        try {
            i.fileReader(file,0);
            i.fileReader(file2,1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(i.invertedIndex.containsKey("agreeable"));

        assertEquals(0, i.invertedIndex.get("agreeable").get(0).fileno);

        int pos = i.invertedIndex.get("agreeable").get(0).position.get(0);
        assertEquals(2, pos);

        assertEquals(1, i.invertedIndex.get("agreeable").get(1).fileno);

        int pos2 = i.invertedIndex.get("agreeable").get(1).position.get(0);

        assertEquals(2, pos2);
    }

    @Test
    public void testDFCounter(){

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("inputFiles/data2.txt").getFile());

        try {
            i.fileReader(file,0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        i.buildTF();

        Double d = i.df.get("agreeable");
        Double d2 = 1.0;
        Double d3 = 0.12216944435630522;

        assertEquals(d2, d);

        Double d4 = i.tf.get("agreeable").get(0);

        assertEquals(d3, d4);


    }

    @Test
    public void testFilesListSize(){
        List<File> fileList = new ArrayList<>();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("inputFiles").getFile());

        i.findFiles( file,fileList);

        assertEquals(3, fileList.size());

    }

}
