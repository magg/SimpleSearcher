package com.example.searcher.service;

import com.example.searcher.domain.dto.Ranking;
import com.example.searcher.domain.dto.Tuple;

import java.text.DecimalFormat;
import java.util.*;

public class SearchService {

    public Indexer index;
    int LIMIT =  10;
    int PERCENT = 100;

    public SearchService(){
        this.index = new Indexer();
    }


    public List<Integer> oneWordsearch(List<String> words, String line) {

        List<Integer> result = new ArrayList<>();

        if (words.size() == 0){
            System.out.println("");
            return result;
        } else if (words.size() > 1) {
            return freeTextSearch(words,line);
        }

        String lookUp = words.get(0);

        List<Tuple> idx = index.invertedIndex.get(lookUp);
        if (idx == null){
            return result;
        }

        for (Tuple t : idx) {
            //answer.add(fileList.get(t.fileno-1).getName());
            result.add(t.fileno);
            //System.out.print(t.fileno);
            //System.out.print(" ");
        }
        //System.out.println();

        //getRanking(words, result);



        return result;

    }

    public List<Ranking> getRanking(List<String> words, List<Integer> docs){

        List<Ranking> rankings = new ArrayList<>();

        Map<Integer, List<Double>> docVectors = new HashMap<>();

        Double [] queryVector = new Double[words.size()];
        Arrays.fill(queryVector, 0.0);

        for (int i = 0 ; i < words.size(); i++){
            String word = words.get(i);

            if (!index.invertedIndex.containsKey(word)) {
                //System.out.println(" word missing "+ word);
                continue;

            }
            queryVector[i] = index.df.get(word) / index.fileList.size();


            List<Tuple> tuples = index.invertedIndex.get(word);

            for (int j = 0 ; j <tuples.size(); j++){
                int doc = tuples.get(j).fileno;

                if (docs.contains(doc)){
                    //List<Double> list = docVectors.getOrDefault(doc, new ArrayList<Double>(Collections.nCopies(words.size(), 0.0)));
                    List<Double> list = new ArrayList<Double>();

                    if (!docVectors.containsKey(doc)){
                        Double[] data = new Double[words.size()];
                        Arrays.fill(data, 0.0);
                        list = Arrays.asList(data);

                    } else {
                        list = docVectors.get(doc);
                    }

                    list.set(i, index.tf.get(word).get(j));

                    docVectors.put(doc, list );
                }

            }
        }

        for (Map.Entry<Integer, List<Double>> entry : docVectors.entrySet()) {
            int doc = entry.getKey();
            List<Double> currentDocVector = entry.getValue();

            Double dot = dotProduct(queryVector, currentDocVector);

            Ranking r = new Ranking(dot, doc);
            rankings.add(r);

        }


        Collections.sort(rankings, new Ranking());

        return rankings;

    }

    public Double dotProduct(Double [] a, List<Double> b){

        Double sum = 0.0;

        if (a.length != b.size()){
            return sum;
        }

        for (int k = 0; k < a.length ; k++){
            sum += b.get(k) * a[k];

        }

        return sum;

    }

    public List<Integer> freeTextSearch(List<String> words, String line){

        List<Integer> result = new ArrayList<Integer>();

        if (words.size() == 0){
            System.out.println("");
            return result;
        }

        //PERCENT = 100;

        int remainder = 100 / words.size();

        Set<Integer> set = new HashSet<>();


        for (String term : words){
            List<Tuple> idx = index.invertedIndex.get(term);
            if (idx == null){
                PERCENT -= remainder;
                continue;
            }
            for (Tuple t : idx) {
                set.add(t.fileno);
            }


        }

        result = new ArrayList<Integer>(set);


        return result;

    }

    public List<Integer> fullTextSearch(List<String> words, String line){

        List<Integer> result = new ArrayList<>();

        //System.out.println(words);

        for (String word : words){

            if (!index.invertedIndex.containsKey(word)){
                return result;
            }

        }

        List<List<Tuple>> tuples =  getTuples(words);
        List<List<Integer>> files = getFilesFromTuples(tuples);
        List<Integer> intersection = computeIntersection(files);

        Map<Integer, List<List<Integer>>> postings = new HashMap<>();

        for (int i = 0 ; i<tuples.size(); i++){
            for (Tuple t :  tuples.get(i)){

                if (intersection.contains(t.fileno)){
                    if (!postings.containsKey(t.fileno)){
                        List<List<Integer>> list = new ArrayList<>();

                        list.add(t.position.stream().collect(java.util.stream.Collectors.toList()));
                        postings.put(t.fileno, list);
                    } else {
                        List<List<Integer>> list = postings.get(t.fileno);
                        list.add(t.position.stream().collect(java.util.stream.Collectors.toList()));
                        postings.put(t.fileno, list);
                    }

                }

            }
        }

        for (Map.Entry<Integer, List<List<Integer>>> entry : postings.entrySet()) {
            List<List<Integer>> list = entry.getValue();
            for (int i = 1; i < list.size(); i++ ){
                for (int j = 0; j < list.get(i).size(); j++){
                    list.get(i).set(j,list.get(i).get(j) - i);
                }

            }
        }

        for (Map.Entry<Integer, List<List<Integer>>> entry : postings.entrySet()) {
            List<Integer> li = computeIntersection(entry.getValue());
            if (li == null || li.isEmpty()) continue;
            else result.add(entry.getKey());
        }


        //getRanking(words,result);

        return result;

    }


    public List<List<Tuple>> getTuples(List<String> words){
        List<List<Tuple>> tuples = new ArrayList<>();

        for(String word: words){
            List<Tuple> bucket = index.invertedIndex.get(word);
            tuples.add(bucket);
        }


        return tuples;
    }


    public static List<List<Integer>> getFilesFromTuples(List<List<Tuple>> tuples){
        List<List<Integer>> files = new ArrayList<>();

        for (List<Tuple> tupleList: tuples){
            List<Integer> fileList = new ArrayList<>();
            for (Tuple t : tupleList){
                fileList.add(t.fileno);
            }
            files.add(fileList);

        }

        return files;

    }

    public <T> List<T> computeIntersection(Iterable<? extends List<? extends T>> lists) {
        Iterator<? extends List<? extends T>> it = lists.iterator();
        Set<T> commonElements = new HashSet<>(it.next());
        while (it.hasNext())
            commonElements.retainAll(new HashSet<>(it.next()));
        return new ArrayList<>(commonElements);
    }


    public List<String> performSearch (String line){


        List<String> queries = new ArrayList<>();

        List<String> words = index.getTerms(line);
        List<Integer> results = new ArrayList<Integer>();
        boolean phrase = false;
        boolean single = false;

        if (words.size() == 0){
            System.out.println("");
        } else if (words.size() == 1) {
            single = true;
            results=oneWordsearch(words,line);

        } else {
            results=fullTextSearch(words,line);
            if (results.isEmpty()){
                results = freeTextSearch(words, line);
            } else {
                phrase = true;
                int count = 0;
                for (int i : results){
                    if (count >= LIMIT) break;
                    count++;
                    String s = index.fileList.get(i-1).getName()+":100%";
                    queries.add(s);
                }


            }
        }

        if (!results.isEmpty() && !phrase && !single ){

            List<Ranking> ranks =  getRanking(words,results);

            int count = 0;
            for (Ranking r : ranks){
                if (count >= LIMIT) break;
                count++;

                Double value =  Double.valueOf(PERCENT);
                Double hundred = 100.0;
                if (Double.compare(value, hundred) > 0){
                    value = 100.0;
                }

                String s = index.fileList.get(r.doc-1).getName()+":"+  new DecimalFormat("#.##").format(value) + "%";
                queries.add(s);

            }

        } else  if (!results.isEmpty() && !phrase ){
            //PERCENT= 100;

            List<Ranking> ranks =  getRanking(words,results);

            int count = 0;
            for (Ranking r : ranks){
                if (count >= LIMIT) break;
                count++;

                Double value =  PERCENT * r.rank * 10.0;
                Double hundred = 100.0;
                if (Double.compare(value, hundred) > 0){
                    value = 100.0;
                }

                String s = index.fileList.get(r.doc-1).getName()+":" + new DecimalFormat("#.##").format(value)+ "%";
                queries.add(s);
            }

        }

        if (results == null || results.isEmpty()) {
            String s = "no matches found";
            queries.add(s);
        }

        PERCENT= 100;


        return queries;
    }



}
