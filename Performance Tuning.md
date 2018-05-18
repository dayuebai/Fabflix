# Insert time of sql files:

## Two optimizations: 
* ## Batch insert:
     Used Batch insert to insert the data. This method disabled the autocommit so that the 
     data base will not commit everytime after insertion(by default in MySQL). Instead, we added to the Batch and commited all insertions just once. In this way, it reduced the time to commit after every insertion so that it is much faster than the naive insert we implemented in the beginning (Naive approach: Writer multiple insertion queries).

* ## Load infile:
    The LOAD DATA INFILE statement reads rows from a text file into a table at a very high speed. It is usually 20 times faster than using INSERT statements. This method enables us to just read arguments from files with precompiled pattern. Therefore we save the time to do lots of interaction with database through driver. It is actually the fastest approach in the three insertion ways we implemented.

---
## Naive Approach:	
* The naive approach is inserting the data row by row, which generates numerous ```INSERT INTO``` queries
---
| Tables | times |
|--------|-------|
|movies | 11 seconds|
|genre| less than 1 seconds|
|genres_in_movies | 9 seconds|
|star | 7 seconds|
|stars_in_movies | 26 seconds|
- ### **Total: 52 seconds;**
---
Load from files:
---
|Tables | times|
|---|---|
|movies | 0.06 second|
|genre 	| less than 0.01 second|
|genres_in_movies 	| 0.08 second|
|star  | 0.04 second|
|stars_in_movies | 0.32 second|
- ### **Total: 0.51 second;**
---
Batch Insert: 
----
- ### **Total: 7.203 seconds;**


# Parse time of XML file: 
- ### We run a java application program to parse these 3 assigned xml files to retrieve proper data we need for scaling our database.
- ### The parsing program running time is 1 minute and 55.910 seconds.
