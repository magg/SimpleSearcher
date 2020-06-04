package com.example.searcher.service;

import com.example.searcher.domain.dto.Tuple;

import java.io.*;
import java.util.*;

public class Indexer {

    Map<String, List<Tuple>> invertedIndex;
    Set<String> stopWords;
    public List<File> fileList;

    Map<String, Double> df;
    Map<String, List<Double>> tf;


    public Indexer () {
        this.invertedIndex = new HashMap<>();
        this.stopWords = new HashSet<>();
        this.fileList = new ArrayList<>();
        this.df = new HashMap<>();
        this.tf = new HashMap<>();

    }


    public void getStopWords() throws IOException {

        InputStream in = getClass().getResourceAsStream("/stop-word-list.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        //BufferedReader reader = new BufferedReader(new FileReader(file));
        String currentLine = reader.readLine();
        while (currentLine != null) {

            currentLine = reader.readLine();
            stopWords.add(currentLine);
        }

        reader.close();


    }


    public void findFiles(File file, List<File> fileList)
    {
        File[] list = file.listFiles();

        if(list!=null)
            for (File fil : list)
            {
                if (fil.isDirectory())
                {
                    findFiles(fil, fileList);
                } else

                {
                    //System.out.println(fil.getAbsolutePath());
                    fileList.add(fil);
                }
            }
    }

    public List<String> getTerms(String line){

        List<String> wordList = new ArrayList<>();

        String[] words = line.toLowerCase().replaceAll("[^a-zA-Z]", " ").split("\\W+");

        for (String s: words){
            if (!stopWords.contains(s)){
                wordList.add(s);
            }
        }
        return wordList;
    }


    public void fileReader(File file, int j) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String currentLine = reader.readLine();
        while (currentLine != null) {

            List<String> terms = getTerms(currentLine);

            for (int i = 0 ; i < terms.size(); i++) {
                String word = terms.get(i);

                List<Tuple> idx = invertedIndex.get(word);
                if (idx == null) {
                    idx = new LinkedList<Tuple>();
                    List<Integer> pos = new ArrayList<>();
                    pos.add(i);
                    idx.add(new Tuple(j, pos));
                    invertedIndex.put(word, idx);

                } else {
                    boolean found = false;
                    for (Tuple t: idx){
                        if (t.fileno == j){
                            found = true;
                            t.position.add(i);

                        }

                    }

                    if (!found) {
                        List<Integer> pos = new ArrayList<>();
                        pos.add(i);
                        idx.add(new Tuple(j, pos));

                    }
                }

            }

            currentLine = reader.readLine();

        }

        reader.close();

    }


    public void buildTF(){

        double norm = 0.0;

        for (Map.Entry<String, List<Tuple>> entry : invertedIndex.entrySet()) {
            for (Tuple t : entry.getValue()){
                norm += Math.pow(t.position.size(), 2);

            }
        }
        norm = Math.sqrt(norm);

        for (Map.Entry<String, List<Tuple>> entry : invertedIndex.entrySet()) {
            Double count = df.getOrDefault(entry.getKey(), 0.0);
            df.put(entry.getKey(), count + 1);

            for (Tuple t : entry.getValue()){
                List<Double> list = tf.getOrDefault(entry.getKey(), new ArrayList<>());
                list.add(t.position.size()/norm);
                tf.put(entry.getKey(), list);
            }
        }

    }

}
