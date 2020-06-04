package com.example.searcher;

import com.example.searcher.service.SearchService;

import java.io.*;
import java.util.*;

public class Searcher {

    public static void main(String args[]) throws Exception {

        if (args.length == 0) throw new IllegalArgumentException("No directory given to index");

        SearchService ss = new SearchService();

        ss.index.getStopWords();

        final File indexableDirectory = new File(args[0]);

        if (!indexableDirectory.isDirectory()){
            System.out.println("ERROR: Please supply a directory");
            System.exit(-1);
        }

        //TODO: Index all files in indexableDirectory
        ss.index.findFiles(indexableDirectory, ss.index.fileList);


        for (int i = 0 ; i < ss.index.fileList.size(); i++){
            ss.index.fileReader(ss.index.fileList.get(i), i+1);
        }

        ss.index.buildTF();

        System.out.println(ss.index.fileList.size() + " files read in directory " + indexableDirectory.getAbsolutePath());


        try (Scanner in = new Scanner(System.in)){

            while(true) {
                System.out.print("search> ");

                final String line = in.nextLine();

                if (line.equals(":quit")) System.exit(0);

                List<String> queries  = ss.performSearch(line);

                for (String s:queries){
                    System.out.println(s);
                }


            }
        }
    }
}

