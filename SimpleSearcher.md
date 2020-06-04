
* Inverted Index

example,  word -> Tuple ( file_number 1, positions in document (0,2))

```
{ ‘web’: [ [1, [0, 2]], [2, [2]] ], 
‘retrieval’: [ [1, [1]] ],
 ‘search’: [ [1, [3]], [2, [0]] ], 
 ‘information’: [ [1, [4]] ], 
 ‘engine’: [ [2, [1]] ], 
 ‘ranking’: [ [2, [3]] ] }
```

* Save inverted index into a File like lucene

* Improve inverted index data structure, currently it uses a Map, could use a Skip List like Lucene

* Missing break statement in fileReader() function

* Add a Stemming algorithm - which transforms a word into its root form - should be included in getTerms() function
```
 cats -> cat
 meeting -> meet
 ```
 
 *  Query types
 
 1) One Word Queries, searching only a single word. 
    * oneWordSearch() function performs a simple query into invertedIndex
 2) Free Text Queries, contain sequence of words separated by space like an actual sentence. The matching documents are the ones that contain any of the query terms, in any order
    * freeTextSearch() , function performs simple query into the invertedIndex and returns a set of documents IDs where any word was found
 3) Full Text Queries, also contain sequence of words, match the phrase exactly in the order specified
    * Get the document list of all query terms: computer: [1, 2, 3], science: [1, 2, 3], and department: [1, 2]. ---> getFilesFromTuples() function
    * Then we intersect all these lists to get the documents that contain all query terms, which is [1, 2]. ---> done in fullTextSearch()
    * First, we get the postings list of the query terms for document 1. Which is computer: [1, [2, 5]], science: [1, [3]], and department: [1, [4, 6]]. ---> Map<Integer, List-List-Integer postings
    * Then, we extract the positions of each query term, and put them in separate lists, resulting in [ [2, 5], [3], [4, 6] ]. Each list corresponds to the positional information of a query term. We don’t touch the first list, but subtract i-1 from the elements in the ith list, resulting in [ [2, 5], [2], [2, 4] ].
    * Finally, we take the intersection of the lists, which is [2]. 
 
 
 
 * Ranking
    * Free Text
 ```
        PERCENT = 100;
        int remainder = 100 / words.size();
        PERCENT-=remainder
  ```
  l
  
   * Full text is 100 % if found
    
    
  ```
 Double value =  PERCENT * r.rank * 10.0;
 Double hundred = 100.0;
  if (Double.compare(value, hundred) > 0){
       value = 100.0;
  }
 ```
              
 
 * Tf-IDF
 
 1) after indexing is complete build DF a simple counter of the number of different documents the term appears in, 
 2) Term frequency is the occurrence count of a term in one particular document only; need to be normalized
 3) idf - num documents / df of term
 4) Dot product to multiply the tf * idf
 
 
 Need to change getRanking function
  ```
 queryVector[i] = index.df.get(word) / index.fileList.size();
 to
 queryVector[i] = index.fileList.size() / index.df.get(word) ;
 ```

