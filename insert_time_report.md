# Insert time of sql files:

## Two optimizations: 
* ## Batch insert:
     Used Batch insert to insert the data. This method disabled the autocommit so that the 
     data base will not commit everytime after insertion. Instead, we added to the Batch and 
     insert all and commited once. In this way, it reduced the time to commit EVERYTIME so that it is much faster than the naive insert. 

* ## Load infile:
    Directly load the file through mysql. This method enables us to just read from files, 
    which conusmes no time for mysql transaction. It takes instant time to read from file and
    write it into database, so it is much faster than commiting to the database. 

---
## Naïve Approach:	
* The naïve approach is inseting the data row by row, which generates numerous ```INSERT INTO``` queries
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
- ### All XML parsing time is about 1 minute and 55.910 seconds.
